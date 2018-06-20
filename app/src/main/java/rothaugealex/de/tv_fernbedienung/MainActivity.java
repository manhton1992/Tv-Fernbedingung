package rothaugealex.de.tv_fernbedienung;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import rothaugealex.de.tv_fernbedienung.SendHttpReqTask.*;
// TODO: move all strings to values/strings.xml and just use references in java & xml code :)
// TODO: Insert Stand-By function ;)

public class MainActivity extends AppCompatActivity {

    static final String TAG = "TVApp-MainAct";

    // GUI components
    private static TextView displayTextView;
    private static ImageButton imageButtonStandby;
    private static Button buttonChannel;

    // GET Parameters
    public static final String PARAM_DEBUG_ON = "debug=1";
    public static final String PARAM_DEBUG_OFF = "debug=0";
    public static final String PARAM_VOLUME = "volume=";
    public static final String PARAM_CHANNEL_CHANGE = "channelMain=";
    public static final String PARAM_CHANNEL_SCAN = "scanChannels=";
    public static final String PARAM_STANDBY = "standby=";
    public static final String PARAM_ZOOM = "zoomMain=";
    public static final String PARAM_PIP = "showPip=";
    public static final String PARAM_PIP_CHANNEL = "channelPip=";


    // HTTP Response Parameters
    public static final String RESP_STATUS = "status";
    public static final String RESP_CHANNELS = "channels";
    public static final String RESP_CHANNEL = "channel";
    public static final String RESP_PROGRAM = "program";

    // Default values
    static final int DEFAULT_VOLUME = 50;
    static final int DEFAULT_PROG_ID = 0;

    // TODO: Use Share Preference to store all of this data persistently
    private static int volume = DEFAULT_VOLUME;
    private static JSONObject programJson;
    public static ArrayList<String> programs;
    public static ArrayList<String> programNames;
    public static ArrayList<String> favorites;
    private static int currentProgId = DEFAULT_PROG_ID;
    private static int currentPipProgId = DEFAULT_PROG_ID;
    private static boolean standby = true;
    private static boolean mute = false;
    private static boolean debug = false;
    private static boolean zoom = false;
    private static boolean pip = false;
    private static boolean pipFocus = false;
    private static int pipId = 0;

    // SharedPref Stuffs
    public static final String SHARED_PREF_TAG = "TVApp-SharedPref";
    public static final String PREF_KEY_VOLUME = "VOLUME";
    public static final String PREF_KEY_CURRENT_PROG = "CURRENT_PROGRAM";
    public static final String PREF_KEY_PROGRAM_JSON = "PROGRAM_JSON";
    public static final String PREF_KEY_FAV_JSON = "FAV_JSON";

    private static SharedPreferences sharedPref;
    private static SharedPreferences.Editor prefEditor;


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
        SendHttpReqTask.executeTask(new SendHttpReqTask(PARAM_CHANNEL_SCAN, new RespHandler() {
            @Override
            public void run() {
                try {
                    // Check status before notify the user that the action is successful
                    if (this.getContent().get(RESP_STATUS).equals("ok"))
                        readProgramJson(this.getContent());
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
    public  static void addCurrentToFav(View view){
        if (standby) return;

        if (programs == null)
            updateDisplay();
        else {
            if (pip && pipFocus) {
                favorites.add(programs.get(currentPipProgId));
                updateDisplay();
                Log.d(TAG, "addCurrentToFav: " + programs.get(currentProgId) + "added");
            }
            else if (!favorites.contains(programs.get(currentProgId))) {
                favorites.add(programs.get(currentProgId));
                updateDisplay();
                Log.d(TAG, "addCurrentToFav: " + programs.get(currentProgId) + "added");
            }
        }
    }


    /**
     * TODO: comment me
     * @param progId
     */
    public static void addToFav(int progId){

        if (standby) return;

        if (programs == null)
            updateDisplay();
        else {
            if (!favorites.contains(programs.get(progId))) {
                favorites.add(programs.get(progId));
                updateDisplay();
                Log.d(TAG, "addCurrentToFav: " + programs.get(progId) + "added");
            }
        }
    }



    /**
     * TODO: comment me
     * @param view
     */
    public void emptyFav(View view){
        favorites.clear();
        updateDisplay();
        Log.d(TAG, "emptyFav: ok");
    }


    /**
     * TODO: comment me
     * @param view
     */
    public void switchDebug(View view){
       SendHttpReqTask.executeTask(new SendHttpReqTask(debug ? PARAM_DEBUG_OFF : PARAM_DEBUG_ON,
               new RespHandler() {
                   @Override
                   public void run() {
                       try {
                           if (this.getContent().get(RESP_STATUS).equals("ok"))
                               debug = !debug;
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
    public void zoom(View view){
        if(standby) return;

        SendHttpReqTask.executeTask(new SendHttpReqTask(PARAM_ZOOM + (zoom ? "0" : "1"),
                new RespHandler() {
                    @Override
                    public void run() {
                        try {
                            if (this.getContent().get(RESP_STATUS).equals("ok"))
                                zoom = !zoom;
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
    public void pip(View view){
        if(standby || programs.isEmpty()) return;

        SendHttpReqTask.executeTask(new SendHttpReqTask(PARAM_PIP + (pip ? "0" : "1&" +
            PARAM_PIP_CHANNEL + programs.get(currentProgId)),
                new RespHandler() {
                    @Override
                    public void run() {
                        try {
                            if (this.getContent().get(RESP_STATUS).equals("ok")) {
                                pip = !pip;
                                buttonChannel.setText("Main Channel");
                                buttonChannel.setVisibility(pip ? View.VISIBLE : View.INVISIBLE);
                                updateDisplay();
                                pipFocus = true;
                                currentPipProgId = currentProgId;
                                buttonChannel.setText(pipFocus ? "Pip Channel" : "Main Channel");
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
    public void pipSwitch(View view){
        if(standby || !pip) return;
        pipFocus = !pipFocus;
        buttonChannel.setText(pipFocus ? "Pip Channel" : "Main Channel");
    }

    /**
     * TODO: comment me
     * @param view
     */
    public void showProgList(View view){
        Intent intent = new Intent(getApplicationContext(), Sender.class);
        // TODO: Different Activities must use Share Preference in order to access (shared) data such as program list

        startActivity(intent);
    }

    /**
     * TODO: comment me
     * @param view
     */
    public void nextProg(View view){

        if (programs.isEmpty() || standby) return;

        if (pip && pipFocus) {
            currentPipProgId = (currentPipProgId + 1) % programs.size();
            String nextProg = programs.get(currentPipProgId);

            SendHttpReqTask.executeTask(new SendHttpReqTask(PARAM_PIP_CHANNEL + nextProg,
                    new RespHandler() {
                @Override
                public void run() {
                    try {
                        // Check status before notify the user that the action is successful
                        if (this.getContent().get(RESP_STATUS).equals("ok")) {
                            updateDisplay();
                            Log.d(TAG, "nextPipProg: ok");
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }));
        }
        else {
            currentProgId = (currentProgId + 1) % programs.size();
            String nextProg = programs.get(currentProgId);

            SendHttpReqTask.executeTask(new SendHttpReqTask(PARAM_CHANNEL_CHANGE + nextProg,
                    new RespHandler() {
                @Override
                public void run() {
                    try {
                        // Check status before notify the user that the action is successful
                        if (this.getContent().get(RESP_STATUS).equals("ok")) {
                            updateDisplay();
                            Log.d(TAG, "nextProg: ok");
                        }
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

        if (programs.isEmpty() || standby) return;

        if (pip && pipFocus) {
            currentPipProgId = ((currentPipProgId - 1) < 0) ? programs.size() - 1 : currentPipProgId - 1;
            String previousProg = programs.get(currentPipProgId);

            SendHttpReqTask.executeTask(new SendHttpReqTask(PARAM_PIP_CHANNEL + previousProg,
                    new RespHandler() {
                @Override
                public void run() {
                    try {
                        // Check status before notify the user that the action is successful
                        if (this.getContent().get(RESP_STATUS).equals("ok")) {
                            updateDisplay();
                            Log.d(TAG, "previousPipProg: ok");
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }));
        }
        else {
            currentProgId = ((currentProgId - 1) < 0) ? programs.size() - 1 : currentProgId - 1;
            String previousProg = programs.get(currentProgId);

            SendHttpReqTask.executeTask(new SendHttpReqTask(PARAM_CHANNEL_CHANGE +
                    previousProg, new RespHandler() {
                @Override
                public void run() {
                    try {
                        // Check status before notify the user that the action is successful
                        if (this.getContent().get(RESP_STATUS).equals("ok")) {
                            updateDisplay();
                            Log.d(TAG, "previousProg: ok");
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }));
        }
    }


    /**
     * Change to arbitrary program given its index
     * @param progId
     */
    public static void changeProg(int progId) {

        if (programs.isEmpty() || standby) return;

        if (pip && pipFocus) {
            currentPipProgId = progId;
            String prog = programs.get(progId);

            SendHttpReqTask.executeTask(new SendHttpReqTask(PARAM_PIP_CHANNEL + prog,
                    new RespHandler() {
                        @Override
                        public void run() {
                            try {
                                // Check status before notify the user that the action is successful
                                if (this.getContent().get(RESP_STATUS).equals("ok")) {
                                    updateDisplay();
                                    Log.d(TAG, "previousPipProg: ok");
                                }
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }));
        }
        else {
            currentProgId = progId;
            String prog = programs.get(progId);

            SendHttpReqTask.executeTask(new SendHttpReqTask(PARAM_CHANNEL_CHANGE +
                    prog, new RespHandler() {
                @Override
                public void run() {
                    try {
                        // Check status before notify the user that the action is successful
                        if (this.getContent().get(RESP_STATUS).equals("ok")) {
                            updateDisplay();
                            Log.d(TAG, "previousProg: ok");
                        }
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
        if (standby) return;
        if (volume >= 100) return;

        SendHttpReqTask.executeTask(
                new SendHttpReqTask(PARAM_VOLUME + ++volume, new RespHandler() {
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
        if (standby) return;
        if (volume <= 0) return;

        SendHttpReqTask.executeTask(
                new SendHttpReqTask(PARAM_VOLUME + --volume, new RespHandler() {
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
        if (standby) return;
        // TODO: Check for valid volume value before sinding HTTP request ;)
        SendHttpReqTask.executeTask(new SendHttpReqTask(PARAM_VOLUME +
                (mute ? volume : "0"), new RespHandler() {
            @Override
            public void run() {
                try {
                    // Check status before notify the user that the action is successful
                    if (this.getContent().get(RESP_STATUS).equals("ok")) {
                        mute = !mute;
                        Log.d(TAG, "mute: ok");
                    }
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
    public void standby(View view)
    {
        if (standby) {
            if (this.programs.isEmpty())
                Toast.makeText(this, "Keiner Sender!", Toast.LENGTH_SHORT).show();
            else {
                imageButtonStandby.setImageResource(R.drawable.pause);
                SendHttpReqTask.executeTask(new SendHttpReqTask(PARAM_STANDBY + "0&" +
                        PARAM_CHANNEL_CHANGE + programs.get(currentProgId), new RespHandler() {
                    @Override
                    public void run() {
                        try {
                            if (this.getContent().get(RESP_STATUS).equals("ok")) {
                                standby = !standby;
                                updateDisplay();
                                Log.d(TAG, "standby = 0");
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }));
            }
        }
        else {
            imageButtonStandby.setImageResource(R.drawable.play);
            SendHttpReqTask.executeTask(new SendHttpReqTask(PARAM_STANDBY + "1",
                    new RespHandler() {
                @Override
                public void run() {
                    try {
                        if (this.getContent().get(RESP_STATUS).equals("ok")) {
                            standby = !standby;
                            updateDisplay();
                            Log.d(TAG, "standby = 1");
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayTextView = findViewById(R.id.textview_display);
        imageButtonStandby = findViewById(R.id.pause);
        buttonChannel = findViewById(R.id.button_channel_main);
        programs = new ArrayList<>();
        programNames = new ArrayList<>();
        favorites = new ArrayList<>();

        sharedPref = this.getSharedPreferences(SHARED_PREF_TAG, MODE_PRIVATE);
        prefEditor = sharedPref.edit();
        this.volume = sharedPref.getInt(PREF_KEY_VOLUME, DEFAULT_VOLUME);
        this.currentProgId = sharedPref.getInt(PREF_KEY_CURRENT_PROG,0);
        String progStr = sharedPref.getString(PREF_KEY_PROGRAM_JSON, null);
        String favStr = sharedPref.getString(PREF_KEY_FAV_JSON, null);

        Log.d(TAG, "onCreate: volume = " + volume );
        Log.d(TAG, "onCreate: currentProgId = " + currentProgId );

        if (progStr != null) {
            try {
                readProgramJson(new JSONObject(progStr));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (favStr != null) {
            try {
                JSONArray favArray = new JSONArray(favStr);
                for (int i=0; i < favArray.length(); i++)
                    favorites.add((String) favArray.get(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        currentProgId = currentProgId >= programs.size() ? 0 : currentProgId;

        // Init Requests
        SendHttpReqTask.executeTask(new SendHttpReqTask(PARAM_VOLUME + volume +
                (programs.isEmpty() ? "&" + PARAM_CHANNEL_SCAN : "") , new RespHandler() {
            @Override
            public void run() {
                if (programs.isEmpty())
                    readProgramJson(this.getContent());
            }
        }));
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Store stuffs
        prefEditor.putInt(PREF_KEY_VOLUME, volume);
        prefEditor.putInt(PREF_KEY_CURRENT_PROG, currentProgId);
        JSONArray favArray = new JSONArray();
        for (String fav: favorites)
            favArray.put(fav);
        prefEditor.putString(PREF_KEY_FAV_JSON, favArray.toString());
        prefEditor.commit();
        Log.d(TAG, "onStop: Pref Saved.");
    }

    /**
     * Update the display screen of the remote control
     */
    private static void updateDisplay() {
        if (standby) {
            displayTextView.setText("Stand By");
            return;
        }
        String msg = programs.isEmpty() ? "Kein Sender!" : programNames.get(currentProgId) +
                (favorites.contains(programs.get(currentProgId)) ? " (fav)" : "");

        // Pip Mode
        if (!programs.isEmpty() && pip)
            msg = msg + "   |   " + programNames.get(currentPipProgId) +
                    (favorites.contains(programs.get(currentPipProgId)) ? " (fav)" : "");

        displayTextView.setText(msg);
    }

    /**
     *
     * @param jsonObject
     */
    private void readProgramJson(JSONObject jsonObject){
        try {
            programs.clear();
            JSONArray channels = jsonObject.getJSONArray(RESP_CHANNELS);
            // Fetch the channel code
            for (int i = 0; i < channels.length(); i++) {
                JSONObject channel = channels.getJSONObject(i);
                programs.add((String) channel.get(RESP_CHANNEL));
            }

            programNames.clear();
            // Fetch the program name
            for (int i = 0; i < channels.length(); i++) {
                JSONObject channel = channels.getJSONObject(i);
                programNames.add((String) channel.get(RESP_PROGRAM));
            }

            Log.d(TAG, "programs size: " + programs.size());

            // Persistently store
            prefEditor.putString(PREF_KEY_PROGRAM_JSON, jsonObject.toString());
            prefEditor.commit();

            Toast.makeText(getBaseContext(),
                    "Senderliste wird aktualisiert", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
