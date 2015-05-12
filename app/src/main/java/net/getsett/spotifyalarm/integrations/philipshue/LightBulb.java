package net.getsett.spotifyalarm.integrations.philipshue;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import net.getsett.spotifyalarm.R;
import net.getsett.spotifyalarm.volleyextensions.RestJsonArrayRequest;

import org.acra.ACRA;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by James on 10/05/2015.
 */
public class LightBulb {

    private final int _id;
    private final Bridge _bridge;
    public static final int MAX_BRIGHTNESS = 255;

    public LightBulb(int id, Bridge bridge){
        _id = id;
        _bridge = bridge;
    }

    public int getId(){
        return _id;
    }

    public void setBrightness(int brightness){

        String url = _bridge.getApiUrl() + "/" + _bridge.getUsername() + "/lights/" + _id + "/state";
        JSONObject body = new JSONObject();
        try {
            if (brightness > 0){
                body.put("on", true);
            }
            else {
                body.put("on", false);
            }
            body.put("bri", brightness);
        }
        catch(JSONException ex){

        }
        RequestFuture<JSONArray> futureBrightnessUpdate = RequestFuture.newFuture();
        RestJsonArrayRequest brightnessUpdate = new RestJsonArrayRequest(
                Request.Method.PUT,
                url,
                body.toString(),
                futureBrightnessUpdate,
                futureBrightnessUpdate
        );

        _bridge.getRequestQueue().add(brightnessUpdate);

        try {
            JSONArray response = futureBrightnessUpdate.get(30, TimeUnit.SECONDS);
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
    }

    public int getBrightness(){
        String url = _bridge.getApiUrl() + "/" + _bridge.getUsername() + "/lights/" + _id;

        RequestFuture<JSONObject> futureBrightnessGet = RequestFuture.newFuture();
        JsonObjectRequest brightnessGet = new JsonObjectRequest(
                url,
                futureBrightnessGet,
                futureBrightnessGet
        );

        _bridge.getRequestQueue().add(brightnessGet);

        try {
            JSONObject response = futureBrightnessGet.get(30, TimeUnit.SECONDS);
            return response.getJSONObject("state").getInt("bri");
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
        catch (JSONException exception) {
            ACRA.getErrorReporter().handleSilentException(exception);
        }

        return -1;
    }
}
