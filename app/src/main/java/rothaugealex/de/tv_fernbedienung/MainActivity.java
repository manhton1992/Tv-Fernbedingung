package rothaugealex.de.tv_fernbedienung;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONObject;

import java.lang.reflect.Executable;

public class MainActivity extends AppCompatActivity {

    static final String TAG = "fernbedienung_MainAct";

    static final String TV_IP_ADDR = "192.168.2.128";
    static final int REQ_TIMEOUT = 10000;

    static final String PARAM_DEBUG_ON = "debug=1";
    static final String PARAM_DEBUG_OFF = "debug=0";
    static final String PARAM_VOLUME = "volume=";
    static final String PARAM_CHANNEL_CHANGE = "channelMain=";

    static final int DEFAULT_VOLUME = 50;

    private int volume = DEFAULT_VOLUME;

    public void toSettings(View view) {
        Intent intent = new Intent(getApplicationContext(), Einstellungen.class);

        startActivity(intent);
    }

    public void favFunction(View view) {
        Intent intent = new Intent(getApplicationContext(), Favorites.class);

        startActivity(intent);
    }

    public void progScan(View view){
        Intent intent = new Intent(getApplicationContext(), Sender.class);

        startActivity(intent);
    }

    public void progNext(View view){


        Toast.makeText(this, "Nächster Sender", Toast.LENGTH_SHORT).show();
    }

    public void progBack(View view){

        Toast.makeText(this, "Vorheriger Sender", Toast.LENGTH_SHORT).show();
    }

    public void volumeUp(View view)
    {
        /*
        TODO: Store current volume in Shared Preference instead of using default value
         */
        new SendHttpReq(PARAM_VOLUME + ++volume, new RespHandler() {
            @Override
            public void run() {
                // TODO: move all strings to values/strings.xml and just use reference here
                Toast.makeText(getBaseContext(),
                        "Fernseher wurde lauter gestellt", Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    public void volumeDown(View view)
    {
        new SendHttpReq(PARAM_VOLUME + --volume, new RespHandler() {
            @Override
            public void run() {
                // TODO: Check status before notify the user that the action is successful
                Toast.makeText(getBaseContext(),
                        "Fernseher wurde leiser gestellt", Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    public void mute(View view)
    {

        Toast.makeText(this, "Fernseher wurde stumm geschaltet", Toast.LENGTH_SHORT).show();

    }

    public void pause(View view)
    {
        ImageButton imagebutton = findViewById(R.id.pause);
        imagebutton.setImageResource(R.drawable.play2);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View view = this.getWindow().getDecorView();


    }


    private class SendHttpReq extends AsyncTask<Void, Void, Boolean> {

        String param;
        JSONObject jsonOut;
        RespHandler respHandler;

        public SendHttpReq(String param, RespHandler respHandler) {
            this.param = param;
            this.respHandler = respHandler;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            Log.d(TAG, "doInBackground: Going to send Request. PARAMS: " + this.param);
            HttpRequest httpReq = new HttpRequest(TV_IP_ADDR, REQ_TIMEOUT);

            JSONObject out = null;
            try {
                out = httpReq.sendHttp(this.param);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            this.jsonOut = out;
            return true;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            Log.d(TAG, "onPostExecute: Going to run Response Handler!");
            this.respHandler.run();
        }

    }

    /**
     *
     */
    interface RespHandler extends Runnable {}
}
