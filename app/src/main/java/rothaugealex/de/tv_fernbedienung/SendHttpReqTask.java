package rothaugealex.de.tv_fernbedienung;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import org.json.JSONObject;

/**
 * Async task for sending HTTP request to the TV and handling the response
 */
public class SendHttpReqTask extends AsyncTask<Void, Void, JSONObject> {

    public static final String TAG = "TVApp-SendHttpReqTask";

    // Connection Parameters
    static final String TV_IP_ADDR = "192.168.2.128";
    static final int REQ_TIMEOUT = 10000;

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

    /**
     * Two running asynTasks will run serially in newer SDK versions.
     * This method helps avoiding this issue.
     * @param task
     */
    @SuppressLint("NewApi")
    public static <P, T extends AsyncTask<P, ?, ?>> void executeTask(T task, P... params) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        } else {
            task.execute(params);
        }
    }

    /**
     *  Every function should implement this handler and pass to the async task as argument.
     *  This handler do the tasks upon receiving the HTTP response.
     */
    public abstract static class RespHandler implements Runnable {
        private JSONObject content = null;

        public void setContent(JSONObject content) {
            this.content = content;
        }

        public JSONObject getContent() {
            return this.content;
        }
    }

}
