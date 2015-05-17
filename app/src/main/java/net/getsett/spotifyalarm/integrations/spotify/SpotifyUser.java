package net.getsett.spotifyalarm.integrations.spotify;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import net.getsett.spotifyalarm.R;

import org.acra.ACRA;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by James on 14/05/2015.
 */
public class SpotifyUser {

    public final String Username;
    private RequestQueue _queue;
    private SpotifyToken token;


    // No argument must mean me
    public SpotifyUser(SpotifyToken token, Context context){
        this.token = token;
        //Get the users username
        String username = null;

        _queue = Volley.newRequestQueue(context);

        RequestFuture<JSONObject> usernameFuture = RequestFuture.newFuture();
        JsonObjectRequest usernameRequest = new SpotifyWebApiRequest(
                Request.Method.GET,
                "https://api.spotify.com/v1/me",
                null,
                token.toString(),
                usernameFuture,
                usernameFuture);

        _queue.add(usernameRequest);

        try {
            JSONObject response = usernameFuture.get(30, TimeUnit.SECONDS);

            try {

                username = response.get("id").toString();
            }
            catch (JSONException exception){

            }
        }
        catch (TimeoutException exception) {

        }
        catch (ExecutionException exception) {

        }
        catch (InterruptedException exception) {

        }

        Username = username;
    }

    public SpotifyUser(SpotifyToken token, String username){
        this.token = token;
        Username = username;
    }

    public List<SpotifyPlaylist> getPlaylists(){
        //Get the users username
        RequestFuture<JSONObject> playlistFuture = RequestFuture.newFuture();
        JsonObjectRequest playlistRequest = new SpotifyWebApiRequest(
                Request.Method.GET,
                "https://api.spotify.com/v1/users/" + Username + "/playlists",
                null,
                token.toString(),
                playlistFuture,
                playlistFuture);

        _queue.add(playlistRequest);

        try {
            JSONObject response = playlistFuture.get(30, TimeUnit.SECONDS);

            try {
                JSONArray jsonPlaylists = response.getJSONArray("items");

                List<SpotifyPlaylist> playlists = new ArrayList<SpotifyPlaylist>();
                for(int i = 0; i < jsonPlaylists.length(); i++){
                    JSONObject jsonPlaylist = jsonPlaylists.getJSONObject(i);
                    playlists.add(new SpotifyPlaylist(jsonPlaylist.getString("name"), jsonPlaylist.getString("uri")));
                }
                return playlists;
            }
            catch (JSONException exception){

            }
        }
        catch (TimeoutException exception) {

        }
        catch (ExecutionException exception) {

        }
        catch (InterruptedException exception) {

        }

        //TODO: Throw some badass exception
        return null;
    }

}
