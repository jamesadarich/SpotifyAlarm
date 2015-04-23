package net.getsett.spotifyalarm;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.acra.ACRA;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class AlarmActivity extends Activity {

    private Handler handler;
    private int _brightness = 0;
    private RequestQueue queue;
    private int lightInterval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        handler = new Handler();
        queue = Volley.newRequestQueue(this);

        int minutes = getIntent().getIntExtra("Minutes", 5);


        long alarmId = getIntent().getLongExtra("AlarmId", 0);

        Alarm alarm = new AlarmRepository().get(alarmId);

        if (alarm != null)
        {
            minutes = alarm.getLightFadeTimeInMinutes();
        }

        lightInterval = (minutes * 60 * 1000) / 255;

        handler.postDelayed(incrementLight, lightInterval);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Runnable incrementLight = new Runnable(){
        public void run()
        {
            String url ="http://192.168.0.2/api/james-test/lights/1/state";

            JSONObject body = new JSONObject();

            try {
                body.put("on", true);
                body.put("bri", _brightness);
            }
            catch(JSONException ex){

            }

            RestJsonArrayRequest request = new RestJsonArrayRequest(Request.Method.PUT, url, body.toString(),
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {

                            if (_brightness < 255) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int progress = (int) Math.floor((((double) _brightness) / 255.0) * 100);
                                        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                                        progressBar.setMax(100);
                                        progressBar.setProgress(progress);

                                        TextView text = (TextView) findViewById(R.id.textView2);
                                        text.setText(progress + "%");
                                    }
                                });

                                _brightness++;
                                handler.postDelayed(incrementLight, lightInterval);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    ACRA.getErrorReporter().handleSilentException(error);
                    handler.postDelayed(incrementLight, lightInterval);
                }
            });
            // Add the request to the RequestQueue.
            queue.add(request);
        }
    };

    private Runnable fadeUpSound = new Runnable(){
        public void run()
        {
            String url = "http://192.168.0.20/api/james-test/lights/1/state";

            JSONObject body = new JSONObject();

            try {
                body.put("on", true);
                body.put("bri", _brightness);
            }
            catch(JSONException ex){

            }

            RestJsonArrayRequest request = new RestJsonArrayRequest(Request.Method.PUT, url, body.toString(),
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {

                            if (_brightness < 255) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int progress = (int) Math.floor((((double) _brightness) / 255.0) * 100);
                                        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                                        progressBar.setMax(100);
                                        progressBar.setProgress(progress);

                                        TextView text = (TextView) findViewById(R.id.textView2);
                                        text.setText(progress + "%");
                                    }
                                });

                                _brightness++;
                                handler.postDelayed(incrementLight, lightInterval);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    ACRA.getErrorReporter().handleSilentException(error);
                    handler.postDelayed(incrementLight, lightInterval);
                }
            });
            // Add the request to the RequestQueue.
            queue.add(request);
        }
    };

}

