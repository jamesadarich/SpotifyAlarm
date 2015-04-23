package net.getsett.spotifyalarm;

import android.os.Handler;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.acra.ACRA;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by James on 17/01/2015.
 */
public class PhillipsHueBridge {

    private String _url;
    private String _username;
    private List<PhillipsHueLightBulb> _lightBulbs;
    private Handler _handler;

    public PhillipsHueBridge(Handler handler) {
        _username = "james-test";
        _lightBulbs = new ArrayList<PhillipsHueLightBulb>();
        _lightBulbs.add(new PhillipsHueLightBulb(1, this));
    }

    public Runnable getUrl = new Runnable(){
        public void run() {
            RestJsonArrayRequest request = new RestJsonArrayRequest(Request.Method.GET, "https://www.meethue.com/api/nupnp", null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                _url = ((JSONObject) response.get(0)).get("internalipaddress").toString();
                            } catch (JSONException e) {

                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    ACRA.getErrorReporter().handleSilentException(error);
                }
            });
        }
    };

    public String getUsername(){
        return _username;
    }

    public void setBulbBrightness(int brightness, int bulbId){
        //handler.post()
    }
}
