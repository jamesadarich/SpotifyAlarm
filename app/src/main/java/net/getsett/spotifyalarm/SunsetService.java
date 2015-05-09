package net.getsett.spotifyalarm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;

import org.acra.ACRA;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
*/
public class SunsetService extends IntentService {

    private String _spotifyUri;
    private String _lightId;

    public SunsetService() {
        super("Sunset Service");
        handler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Gets data from the incoming Intent

        queue = Volley.newRequestQueue(this.getApplicationContext());
        int minutes = intent.getIntExtra("Minutes", 5);
        _spotifyUri = intent.getStringExtra("SpotifyUri");
        lightInterval = (minutes * 60 * 1000) / 255;
        _lightId = intent.getStringExtra("HueLightId");

        //_lightBridge = new PhillipsHueBridge(handler);

        PowerManager pm = (PowerManager) this.getApplicationContext().getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Sunset Service");
        wakeLock.acquire();

        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Sunset")
                .setContentText("Sunset in progress")
                .setSmallIcon(R.drawable.ic_launcher);

        mNotifyManager.notify(
                notifyId,
                mBuilder.build());
        handler.post(setUp);

        Config playerConfig = new Config(this, intent.getStringExtra("SpotifyToken"), getString(R.string.spotifyApiKey));
        _player =  Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
            @Override
            public void onInitialized(Player player) {
                //_player.addConnectionStateCallback();
                //_player.addPlayerNotificationCallback();
                _player.play(_spotifyUri);
                //handler.postDelayed(decrementLight, lightInterval);
            }
            @Override
            public void onError(Throwable throwable) {
                //Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
            }
        });
    }

    private Player _player;
    private PhillipsHueBridge _lightBridge;
    private Handler handler;
    private int _brightness = 255;
    private RequestQueue queue;
    private int lightInterval;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private int notifyId = 1;
    private PowerManager.WakeLock wakeLock;
    private String _ipAddress;
    private String _username;

    private Runnable decrementLight = new Runnable(){
        public void run()
        {
            String url = "http://" + _ipAddress + "/api/" + _username + "/lights/" + _lightId + "/state";

            JSONObject body = new JSONObject();

            try {
                if (_brightness > 0){
                    body.put("on", true);
                }
                else {
                    body.put("on", false);
                }
                body.put("bri", _brightness);
            }
            catch(JSONException ex){

            }

            RestJsonArrayRequest request = new RestJsonArrayRequest(Request.Method.PUT, url, body.toString(),
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {

                            if (_brightness > 0) {

                                int progress = (int) Math.floor((((double) _brightness) / 255.0) * 100);
                                mBuilder.setProgress(100, progress, false);
                                // Displays the progress bar for the first time.
                                mNotifyManager.notify(notifyId, mBuilder.build());

                                _brightness--;

                                handler.postDelayed(decrementLight, lightInterval);

                                //Adjust the spotify volume
                                if (_brightness > 25) {

                                    AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                                    int volumeindex = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 2;
                                    volumeindex = volumeindex * _brightness;
                                    volumeindex = volumeindex / 255;
                                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumeindex, 0);
                                }
                            }
                            else if (_brightness < 25){
                                _player.pause();
                                Spotify.destroyPlayer(this);

                                //Return the volume to where it should be
                            }
                            else
                            {

                                // When the loop is finished, updates the notification
                                mBuilder.setContentText("Sunset")
                                        // Removes the progress bar
                                        .setProgress(0,0,false);
                                mNotifyManager.notify(notifyId, mBuilder.build());
                                wakeLock.release();

                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    ACRA.getErrorReporter().handleSilentException(error);
                    handler.postDelayed(decrementLight, lightInterval);
                }
            });
            // Add the request to the RequestQueue.
            queue.add(request);
        }
    };
    public Runnable setUp = new Runnable(){
        public void run() {
            JsonArrayRequest request = new JsonArrayRequest("https://www.meethue.com/api/nupnp",
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                _ipAddress = ((JSONObject) response.get(0)).get("internalipaddress").toString();
                                _username = "james-test";
                                handler.post(decrementLight);
                            } catch (JSONException e) {

                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    ACRA.getErrorReporter().handleSilentException(error);
                }
            });

            queue.add(request);
        }
    };
}
