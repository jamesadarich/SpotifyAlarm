package net.getsett.spotifyalarm.integrations.philipshue;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import net.getsett.spotifyalarm.R;

import org.acra.ACRA;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by James on 10/05/2015.
 */
public class HueBridge {

    private RequestQueue _requestQueue;
    private Context _context;
    private String _cachedApiUrl;

    public HueBridge(Context context){
        _requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        _context = context;
    }

    public String getUsername() {
        return _context.getString(R.string.hue_username);
    }

    public RequestQueue getRequestQueue(){
        return _requestQueue;
    }

    public String getApiUrl() {
        RequestFuture<JSONArray> futureApiUrlRequest = RequestFuture.newFuture();

        JsonArrayRequest apiUrlRequest = new JsonArrayRequest(
            "https://www.meethue.com/api/nupnp",
            futureApiUrlRequest,
            futureApiUrlRequest
        );

        _requestQueue.add(apiUrlRequest);

        try {
            JSONArray response = futureApiUrlRequest.get(30, TimeUnit.SECONDS);
            _cachedApiUrl = "http://" + ((JSONObject) response.get(0)).getString("internalipaddress")+ "/api";
            return _cachedApiUrl;
        }
        catch (TimeoutException exception) {
            return _cachedApiUrl;
            //ACRA.getErrorReporter().handleSilentException(exception);
        }
        catch (InterruptedException exception) {
            ACRA.getErrorReporter().handleSilentException(exception);
        }
        catch (ExecutionException exception) {
            ACRA.getErrorReporter().handleSilentException(exception);
        }
        catch (JSONException exception) {
            ACRA.getErrorReporter().handleSilentException(exception);
        }

        return null;
    }

    public List<HueLightBulb> getAllLightBulbs(){

        String url = getApiUrl() + "/" + getUsername() + "/lights";

        RequestFuture<JSONObject> futureGetLightBulbs = RequestFuture.newFuture();

        JsonObjectRequest getLightBulbs = new JsonObjectRequest(
                url,
                futureGetLightBulbs,
                futureGetLightBulbs
        );

        _requestQueue.add(getLightBulbs);
        try {
            JSONObject response = futureGetLightBulbs.get(30, TimeUnit.SECONDS);
            Iterator<String> keys = response.keys();
            List<HueLightBulb> lightBulbs = new ArrayList<HueLightBulb>();
            while(keys.hasNext()) {
                Integer key = Integer.parseInt(keys.next());
                try {
                    lightBulbs.add(new HueLightBulb(key, response.getJSONObject(key.toString()).getString("name"), this));
                }
                catch (JSONException exception){

                }
            }
            return lightBulbs;
        }
        catch (TimeoutException exception) {
            ACRA.getErrorReporter().handleSilentException(exception);
        }
        catch (InterruptedException exception) {
            ACRA.getErrorReporter().handleSilentException(exception);
        }
        catch (ExecutionException exception) {
            ACRA.getErrorReporter().handleSilentException(exception);
        }

        return null;
    }

    public HueLightBulb getLightBulbById(int id){
        String url = getApiUrl() + "/" + getUsername() + "/lights/" + id;

        RequestFuture<JSONObject> futureGetLightBulbs = RequestFuture.newFuture();

        JsonObjectRequest getLightBulbs = new JsonObjectRequest(
                url,
                futureGetLightBulbs,
                futureGetLightBulbs
        );

        _requestQueue.add(getLightBulbs);
        try {
            JSONObject response = futureGetLightBulbs.get(30, TimeUnit.SECONDS);
            return new HueLightBulb(id, response.getString("name"), this);
        }
        catch (TimeoutException exception) {
            ACRA.getErrorReporter().handleSilentException(exception);
        }
        catch (InterruptedException exception) {
            ACRA.getErrorReporter().handleSilentException(exception);
        }
        catch (ExecutionException exception) {
            ACRA.getErrorReporter().handleSilentException(exception);
        }
        catch (JSONException exception){

        }

        return null;
    }
}
