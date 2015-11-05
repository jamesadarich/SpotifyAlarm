package net.getsett.spotifyalarm.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;

import net.getsett.spotifyalarm.R;
import net.getsett.spotifyalarm.integrations.philipshue.HueBridge;
import net.getsett.spotifyalarm.integrations.philipshue.HueLightBulb;
import net.getsett.spotifyalarm.integrations.spotify.SpotifyTokenGenerator;
import net.getsett.spotifyalarm.models.Options;
import net.getsett.spotifyalarm.volleyextensions.RestJsonArrayRequest;

import org.acra.ACRA;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class SunriseService extends IntentService {
    public SunriseService() {
        super("Sunrise Service");
    }

    private String _spotifyUri;
    private Player _player;
    private Options _options;


    @Override
    protected void onHandleIntent(Intent intent) {
        // Gets data from the incoming Intent

        _options = (Options)intent.getSerializableExtra("options");

        //_lightBridge = new PhillipsHueBridge(handler);

        PowerManager pm = (PowerManager) this.getApplicationContext().getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Sunrise Service");
        wakeLock.acquire();

        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Sunrise")
                .setContentText("Sunrise in progress")
                .setSmallIcon(R.drawable.ic_launcher);

        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);

        if (_options.SpotifyOptions != null) {
            SpotifyTokenGenerator generator = new SpotifyTokenGenerator(this);
            String token = generator.refreshToken(_options.SpotifyOptions.RefreshToken).toString();
            Config playerConfig = new Config(this, token, getString(R.string.spotifyApiKey));
            _player = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
                @Override
                public void onInitialized(Player player) {
                    //_player.addConnectionStateCallback();
                    //_player.addPlayerNotificationCallback();
                    _player.setShuffle(_options.SpotifyOptions.Randomise);
                    _player.setRepeat(true);
                    _player.play(_options.SpotifyOptions.PlaylistUri);

                    //bug in player means first track is always first in playlist so skip if random
                    if (_options.SpotifyOptions.Randomise){
                        _player.skipToNext();
                    }
                    //handler.postDelayed(decrementLight, lightInterval);
                }

                @Override
                public void onError(Throwable throwable) {
                    //Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                }
            });
        }

        mNotifyManager.notify(
                notifyId,
                mBuilder.build());
        //handler.post(setUp);

        new SunsetTask().execute();
    }

        private class SunsetTask extends AsyncTask<Void, Integer, Void> {
        /** The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute() */
        protected Void doInBackground(Void... nothing) {
            //loadImageFromNetwork(urls[0]);
            HueBridge bridge = new HueBridge(getApplicationContext());
            HueLightBulb lightBulb = bridge.getLightBulbById(_options.HueOptions.LightBulbId);

            //Initially set the brightness to 0
            lightBulb.setBrightness(0);

            try {
                if (true) {
                    throw new Exception("report test");
                }
            }
            catch(Exception e) {
                ACRA.getErrorReporter().handleSilentException(e);
            }


            int brightness = lightBulb.getBrightness();
            while (brightness < 255) {
                try {
                    brightness++;
                    double progress = (double) brightness / 255.0;
                    int progressPercent = (int) Math.floor(progress * 100);
                    publishProgress(progressPercent);
                    int interval = (int) (progress * (_options.TimeToSunset * 60 * 1000)) / brightness;
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException exception) {

                    }
                    lightBulb.setBrightness(brightness);

                    //Adjust the spotify volume
                    if (_options.SpotifyOptions != null) {
                        if (progressPercent > 10) {

                            AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                            int volumeindex = (int) ((double) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * 0.75);
                            volumeindex = volumeindex * progressPercent;
                            volumeindex = volumeindex / 100;
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumeindex, 0);
                        }
                    /*else {
                        _player.pause();
                        Spotify.destroyPlayer(this);

                        //TODO Return the volume to where it should be
                    }*/
                    }
                } catch (Exception e) {
                   // ACRA.getErrorReporter().handleSilentException(e);
                    Integer x = 1;
                }
            }


            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
            mBuilder.setProgress(100, progress[0], false);
            // Displays the progress bar for the first time.
            mNotifyManager.notify(notifyId, mBuilder.build());
        }

        /** The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground() */
        protected void onPostExecute(Void result) {
            //mImageView.setImageBitmap(result);
            // When the loop is finished, updates the notification
            mBuilder.setContentText("Sunset")
                    // Removes the progress bar
                    .setProgress(0,0,false);
            mNotifyManager.notify(notifyId, mBuilder.build());
            wakeLock.release();
        }
    }

    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private int notifyId = 1;
    private PowerManager.WakeLock wakeLock;
}
