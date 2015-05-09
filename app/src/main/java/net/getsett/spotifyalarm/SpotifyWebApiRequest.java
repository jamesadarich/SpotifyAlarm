package net.getsett.spotifyalarm;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by James on 26/04/2015.
 */
public class SpotifyWebApiRequest extends JsonObjectRequest {

    private String _authToken;

    public SpotifyWebApiRequest(int method, String url, JSONObject jsonRequest, String authToken,
                       Response.Listener<JSONObject> listener,
                       Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
        _authToken = authToken;
    }

    public SpotifyWebApiRequest(String url, JSONObject jsonRequest, String authToken,
                       Response.Listener<JSONObject> listener,
                       Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
        _authToken = authToken;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {

        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", "Bearer " + _authToken);
        return headerMap;
    }
}
