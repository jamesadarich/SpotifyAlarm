package net.getsett.spotifyalarm.integrations.spotify;

import android.content.Context;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import net.getsett.spotifyalarm.R;
import net.getsett.spotifyalarm.integrations.oauth.OAuth2TokenRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by James on 14/05/2015.
 */
public class SpotifyTokenGenerator {

    private final RequestQueue _queue;
    private final Context _context;

    public SpotifyTokenGenerator(Context context) {
        _context = context;
        _queue = Volley.newRequestQueue(context);
    }

    public SpotifyToken getToken(String code){

        String clientString = _context.getString(R.string.spotifyApiKey) + ":" + _context.getString(R.string.spotifyClientSecret);
        String base64ClientString = null;
        try {
            byte[] clientStringBytes = clientString.getBytes("UTF-8");
            base64ClientString = Base64.encodeToString(clientStringBytes, Base64.NO_WRAP);
        }
        catch (UnsupportedEncodingException exception) {
        }

        RequestFuture<JSONObject> futureToken = RequestFuture.newFuture();

        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("grant_type", "authorization_code");
        paramMap.put("code", code);
        paramMap.put("redirect_uri", _context.getString(R.string.spotifyRedirectUri));
        paramMap.put("client_id", _context.getString(R.string.spotifyApiKey));
        paramMap.put("client_secret", _context.getString(R.string.spotifyClientSecret));

        Request<JSONObject> tokenRequest = new OAuth2TokenRequest(
                Request.Method.POST,
                "https://accounts.spotify.com/api/token",
                base64ClientString,
                paramMap,
                futureToken,
                futureToken);

        _queue.add(tokenRequest);

        try {
            return new SpotifyToken(futureToken.get(30, TimeUnit.SECONDS), this);
        }
        catch (InterruptedException exception){

        }
        catch (TimeoutException exception){

        }
        catch (ExecutionException exception){

            exception.toString();
        }

        return null;
    }

    public SpotifyToken refreshToken(String refreshToken){

        String clientString = _context.getString(R.string.spotifyApiKey) + ":" + _context.getString(R.string.spotifyClientSecret);
        String base64ClientString = null;
        try {
            byte[] clientStringBytes = clientString.getBytes("UTF-8");
            base64ClientString = Base64.encodeToString(clientStringBytes, Base64.NO_WRAP);
        }
        catch (UnsupportedEncodingException exception) {
        }

        RequestFuture<JSONObject> futureToken = RequestFuture.newFuture();

        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("grant_type", "refresh_token");
        paramMap.put("refresh_token", refreshToken);
        paramMap.put("redirect_uri", _context.getString(R.string.spotifyRedirectUri));
        paramMap.put("client_id", _context.getString(R.string.spotifyApiKey));
        paramMap.put("client_secret", _context.getString(R.string.spotifyClientSecret));
        /*
        paramMap.put("redirect_uri", _context.getString(R.string.spotifyRedirectUri));
        paramMap.put("client_id", _context.getString(R.string.spotifyApiKey));
        paramMap.put("client_secret", _context.getString(R.string.spotifyClientSecret));
        */

        Request<JSONObject> tokenRequest = new OAuth2TokenRequest(
                Request.Method.POST,
                "https://accounts.spotify.com/api/token",
                base64ClientString,
                paramMap,
                futureToken,
                futureToken);

        _queue.add(tokenRequest);

        try {
            return new SpotifyToken(futureToken.get(30, TimeUnit.SECONDS), this);
        }
        catch (InterruptedException exception){

        }
        catch (TimeoutException exception){

        }
        catch (ExecutionException exception){

            exception.toString();
        }

        return null;
    }
}
