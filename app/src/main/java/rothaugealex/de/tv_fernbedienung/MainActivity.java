package rothaugealex.de.tv_fernbedienung;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.tv.TvContract;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

// TODO: move all strings to values/strings.xml and just use references in java & xml code :)
// TODO: Insert Stand-By function ;)

public class MainActivity extends AppCompatActivity {

    static final String TAG = "fernbedienung_MainAct";

    // Connection Parameters
    static final String TV_IP_ADDR = "172.16.200.38";
    static final int REQ_TIMEOUT = 10000;

    // GET Parameters
    static final String PARAM_DEBUG_ON = "debug=1";
    static final String PARAM_DEBUG_OFF = "debug=0";
    static final String PARAM_VOLUME = "volume=";
    static final String PARAM_CHANNEL_CHANGE = "channelMain=";
    static final String PARAM_CHANNEL_SCAN = "scanChannels=";
    static final String PARAM_POWER_OFF = "powerOff=";


    // HTTP Response Parameters
    static final String RESP_STATUS = "status";
    static final String RESP_CHANNELS = "channels";
    static final String RESP_CHANNEL = "channel";

    // Default values
    static final int DEFAULT_VOLUME = 50;
    static final int DEFAULT_PROG_ID = 0;

    // TODO: Use Share Preference to store all of this data persistently
    private int volume = DEFAULT_VOLUME;
    private ArrayList<String> programs = null;
    private int currentProgId = DEFAULT_PROG_ID;


    public void toSettings(View view) {
        Intent intent = new Intent(getApplicationContext(), Einstellungen.class);

        startActivity(intent);
    }

    public void favFunction(View view) {
        Intent intent = new Intent(getApplicationContext(), Favorites.class);

        startActivity(intent);
    }

    /**
     * TODO: comment me
     * @param view
     */
    public void scanProgList(View view){
        executeTask(new SendHttpReqTask(PARAM_CHANNEL_SCAN, new RespHandler() {
            @Override
            public void run() {
                try {
                    // Check status before notify the user that the action is successful
                    if (this.getContent().get(RESP_STATUS).equals("ok")) {
                        programs = new ArrayList<>();
                        JSONArray channels = this.getContent().getJSONArray(RESP_CHANNELS);
                        // Fetch only the channel name for now
                        // TODO: Fetch other data if needed
                        for (int i=0; i < channels.length(); i++) {
                            JSONObject channel = channels.getJSONObject(i);
                            programs.add((String) channel.get(RESP_CHANNEL));
                        }

                        Toast.makeText(getBaseContext(),
                                "Senderliste wird aktualisiert", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    /**
     * TODO: comment me
     * @param view
     */
    public void showProgList(View view){
        Intent intent = new Intent(getApplicationContext(), Sender.class);


        StringBuilder stringBuilder = new StringBuilder();

        for (String s : programs) {
            stringBuilder.append(s);
            stringBuilder.append(",");
        }

        SharedPreferences senderliste = getSharedPreferences("PREFS", 0);
        SharedPreferences.Editor editor = senderliste.edit();
        editor.putString("Sender", stringBuilder.toString());
        editor.commit();


        startActivity(intent);



    }

    /**
     * TODO: comment me
     * @param view
     */
    public void nextProg(View view){
        if (this.programs == null)
            Toast.makeText(this, "Keiner Sender!", Toast.LENGTH_SHORT).show();
        else {
            // Just a hack for now
            // TODO: do it in a more beautiful way
            currentProgId = (currentProgId + 1) % programs.size();
            String nextProg = programs.get(currentProgId);

            executeTask(new SendHttpReqTask(PARAM_CHANNEL_CHANGE + nextProg, new RespHandler() {
                @Override
                public void run() {
                    try {
                        // Check status before notify the user that the action is successful
                        if (this.getContent().get(RESP_STATUS).equals("ok"))
                            Log.d(TAG, "nextProg: ok");
                            // TODO: Do something further?
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }));
        }
    }

    /**
     * TODO: comment me
     * @param view
     */
    public void previousProg(View view){
        if (this.programs == null)
            Toast.makeText(this, "Keiner Sender!", Toast.LENGTH_SHORT).show();
        else {
            // Just a hack for now
            // TODO: do it in a more beautiful way
            currentProgId = ((currentProgId - 1) < 0) ? programs.size() - 1 : currentProgId - 1;
            String previousProg = programs.get(currentProgId);

            executeTask(new SendHttpReqTask(PARAM_CHANNEL_CHANGE + previousProg, new RespHandler() {
                @Override
                public void run() {
                    try {
                        // Check status before notify the user that the action is successful
                        if (this.getContent().get(RESP_STATUS).equals("ok"))
                            Log.d(TAG, "previousProg: ok");
                             // TODO: Do something further?
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }));
        }
    }

    /**
     * TODO: comment me
     * @param view
     */
    public void volumeUp(View view)
    {
        // TODO: Check for valid volume value before sending HTTP request;)
        /*
        TODO: Store current volume in Shared Preference instead of using default value
         */
        executeTask(new SendHttpReqTask(PARAM_VOLUME + ++volume, new RespHandler() {
            @Override
            public void run() {
                try {
                    // Check status before notify the user that the action is successful
                    if (this.getContent().get(RESP_STATUS).equals("ok"))
                        Log.d(TAG, "volumeUp: ok");
                        // TODO: Do something further?
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    /**
     * TODO: comment me
     * @param view
     */
    public void volumeDown(View view)
    {
        // TODO: Check for valid volume value before sinding HTTP request ;)
        executeTask(new SendHttpReqTask(PARAM_VOLUME + --volume, new RespHandler() {
            @Override
            public void run() {
                try {
                    // Check status before notify the user that the action is successful
                    if (this.getContent().get(RESP_STATUS).equals("ok"))
                        Log.d(TAG, "volumeDown: ok");
                        // TODO: Do something further?
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    /**
     * TODO: comment me
     * @param view
     */
    public void mute(View view)
    {
        // TODO
        //Toast.makeText(this, "Fernseher wurde stumm geschaltet", Toast.LENGTH_SHORT).show();
        executeTask(new SendHttpReqTask(PARAM_VOLUME + 0, new RespHandler() {
            @Override
            public void run() {
                try {
                    // Check status before notify the user that the action is successful
                    if (this.getContent().get(RESP_STATUS).equals("ok"))
                        Log.d(TAG, "volumeMute: ok");
                    // TODO: Do something further?
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));

    }

    /**
     * TODO: comment me
     * @param view
     */
    public void pause(View view)
    {
        ImageButton imagebutton = findViewById(R.id.pause);
        imagebutton.setImageResource(R.drawable.play2);
        // TODO
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View view = this.getWindow().getDecorView();

        // Initialize the program list
        this.scanProgList(view);


        // For Debug only
        new SendHttpReqTask(PARAM_DEBUG_ON, new RespHandler() {
            @Override
            public void run() {
                // Do nothing
            }
        }).execute();

    }

    /**
     * Two running asynTasks will run serially in newer SDK versions.
     * This method helps avoiding this issue.
     * @param task
     */
    @SuppressLint("NewApi")
    static <P, T extends AsyncTask<P, ?, ?>> void executeTask(T task, P... params) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        } else {
            task.execute(params);
        }
    }


    /**
     * Async task for sending HTTP request to the TV and handling the response
     */
    public class SendHttpReqTask extends AsyncTask<Void, Void, JSONObject> {

        String param;
        RespHandler respHandler;

        public SendHttpReqTask(String param, RespHandler respHandler) {
            this.param = param;
            this.respHandler = respHandler;
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground: Going to send Request. PARAMS: " + this.param);
            HttpRequest httpReq = new HttpRequest(TV_IP_ADDR, REQ_TIMEOUT);
            JSONObject jsonOut = null;
            try {
                jsonOut = httpReq.sendHttp(this.param);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return jsonOut;
        }

        @Override
        protected void onPostExecute(JSONObject jsonOut) {
            super.onPostExecute(jsonOut);

            Log.d(TAG, "onPostExecute: Going to run Response Handler!");
            this.respHandler.setContent(jsonOut);
            respHandler.run();

        }
    }

    /**
     *  Every function should implement this handler and pass to the async task as argument.
     *  This handler do the tasks upon receiving the HTTP response.
     */
    public abstract class RespHandler implements Runnable {
        private JSONObject content = null;

        public void setContent(JSONObject content) {
            this.content = content;
        }

        public JSONObject getContent() {
            return this.content;
        }
    }
}
