package com.abdelazim.x.uber_rider;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;


public class MainActivity extends AppCompatActivity {

    private static final String SOCKET_QUERY = "SecretKey=5sb-6v2-oc09&Authorization=40";
    String SERVER_URI = "http://35.228.36.71:5511";

    private Socket mSocket;


    private Emitter.Listener responseHandler = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            final String string = args[0].toString();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "response: " + string, Toast.LENGTH_SHORT).show();
                    Log.i("HHH", string);
                }
            });
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // YourDataTask task = new YourDataTask();
       // task.execute();

        try {
            IO.Options opt = new IO.Options();
            opt.query = SOCKET_QUERY;
            this.mSocket = IO.socket(SERVER_URI, opt);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        this.mSocket.on("response", responseHandler);

        this.mSocket.connect();

        this.mSocket.emit("request", "/refreshCoordinates/130/188880,1999960");

    }
}

class YourDataTask extends AsyncTask<Void, Void, JSONObject> {
    @Override
    protected JSONObject doInBackground(Void... params) {

        String str = "http://35.228.36.71:5511/api/getProfile";
        URLConnection urlConn = null;
        BufferedReader bufferedReader = null;
        try {
            URL url = new URL(str);
            urlConn = url.openConnection();
            bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
Log.v("HHH",stringBuffer.toString());
            return new JSONObject(stringBuffer.toString());
        } catch (Exception ex) {
            Log.v("HHH", "yourDataTask", ex);
            return null;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onPostExecute(JSONObject response) {
        if (response != null) {
            try {
                Log.v("HHH", "Success: " + response.getString("firstName"));
            } catch (JSONException ex) {
                Log.v("HHH", "Failure", ex);
            }
        }
    }
}