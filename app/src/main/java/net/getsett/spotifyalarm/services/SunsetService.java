package net.getsett.spotifyalarm.services;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;
import net.getsett.spotifyalarm.R;
import net.getsett.spotifyalarm.integrations.philipshue.HueBridge;
import net.getsett.spotifyalarm.integrations.philipshue.HueLightBulb;
import net.getsett.spotifyalarm.models.Options;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
*/
public class SunsetService extends IntentService {

    private Options _options;
    private Player _player;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private int notifyId = 1;
    private PowerManager.WakeLock wakeLock;
    private Context _context;

    public SunsetService() {
        super("Sunset Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Gets data from the incoming Intent

        _options = (Options)intent.getSerializableExtra("options");

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

        //If the User selected to play a Spotify playlist then play what they selected
        if (_options.SpotifyOptions != null) {

            Config playerConfig = new Config(this, _options.SpotifyOptions.Token, getString(R.string.spotifyApiKey));
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

        new SunsetTask().execute();
    }

    private class SunsetTask extends AsyncTask<Void, Integer, Void> {
        /** The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute() */
        protected Void doInBackground(Void... nothing) {
            //loadImageFromNetwork(urls[0]);
            HueBridge bridge = new HueBridge(getApplicationContext());
            HueLightBulb lightBulb = bridge.getLightBulbById(_options.HueOptions.LightBulbId);


            int brightness = lightBulb.getBrightness();
            while (brightness > 0) {
                double progress = (double) brightness / 255.0;
                int progressPercent = (int) Math.floor(progress * 100);
                publishProgress(progressPercent);
                int interval = (int) (progress * (_options.TimeToSunset * 60 * 1000)) / brightness;
                try {
                    Thread.sleep(interval);
                }
                catch (InterruptedException exception){

                }
                brightness--;
                lightBulb.setBrightness(brightness);

                //Adjust the spotify volume
                if (_options.SpotifyOptions != null) {
                    if (progressPercent > 10) {

                        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                        int volumeindex = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 2;
                        volumeindex = volumeindex * progressPercent;
                        volumeindex = volumeindex / 100;
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumeindex, 0);
                    } else {
                        _player.pause();
                        Spotify.destroyPlayer(this);

                        //TODO Return the volume to where it should be
                    }
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
}
