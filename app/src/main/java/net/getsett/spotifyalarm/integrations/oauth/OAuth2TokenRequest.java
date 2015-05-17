package net.getsett.spotifyalarm.integrations.oauth;

import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import net.getsett.spotifyalarm.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by James on 16/05/2015.
 */
public class OAuth2TokenRequest extends Request<JSONObject> {

    private Response.Listener<JSONObject> responseListener;
    private Map<String, String> params;
    private String authorisationString;

    public OAuth2TokenRequest(int method,
                              String url,
                              String authorisationString,
                              Map<String, String> params,
                              Response.Listener<JSONObject> responseListener,
                              Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.responseListener = responseListener;
        this.params = params;
        this.authorisationString = authorisationString;
    }

    protected Map<String, String> getParams()
            throws com.android.volley.AuthFailureError {
        return params;
    };

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String,String> params = new HashMap<String, String>();
        params.put("Authentication","Basic " + authorisationString);
        return params;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        // TODO Auto-generated method stub
        responseListener.onResponse(response);
    }
}
