package net.getsett.spotifyalarm;

import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.acra.ACRA;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by James on 17/01/2015.
 */
public class PhillipsHueLightBulb {

    private int _id;
    private PhillipsHueBridge _bridge;
    private RequestQueue _queue;

    public PhillipsHueLightBulb(int id, PhillipsHueBridge bridge){
        _id = id;
        _bridge = bridge;
    }

    public int getId(){
        return _id;
    }

    public void turnOn(){

    }

    /*
    public void setBrightness(final int brightness){
        String url = _bridge.getUrl() + _bridge.getUsername() + "/lights/" + _id + "/state";

        JSONObject body = new JSONObject();
        try {
            body.put("bri", brightness);
        }
        catch(JSONException ex){ }

        RestJsonArrayRequest request = new RestJsonArrayRequest(Request.Method.PUT, url, body,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ACRA.getErrorReporter().handleSilentException(error);
                handler.postDelayed(decrementLight, lightInterval);
            }
        });

        // Add the request to the RequestQueue.
        _queue.add(request);
    }
    */
}
