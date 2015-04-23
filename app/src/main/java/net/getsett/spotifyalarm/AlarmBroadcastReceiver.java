package net.getsett.spotifyalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.widget.SeekBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by James on 15/01/2015.
 */
public class AlarmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent serviceIntent = new Intent(context, SunriseService.class);
        serviceIntent.putExtra("Minutes", serviceIntent.getIntExtra("Minutes", 5));

        context.startService(serviceIntent);
        /*
        PowerManager pm = (PowerManager)     context.getSystemService(Context.POWER_SERVICE);

        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");
        wl.acquire();
        Intent puzzleIntent = new Intent(context, AlarmActivity.class);

        puzzleIntent.putExtra("Minutes", intent.getIntExtra("Minutes", 5));

        puzzleIntent.putExtra("AlarmId", intent.getLongExtra("AlarmId", 0));
        puzzleIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                + Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(puzzleIntent);
        */

        /*MediaPlayer mp = MediaPlayer.create(context, android.R.raw.ferry_sound);
        mp.start();*/

        /*
        PowerManager pm = (PowerManager)     context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");
        wl.acquire();
        Intent puzzleIntent = new Intent(context, MathPuzzle.class);
        puzzleIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                + Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(puzzleIntent);

        _context = context;
        lightsUp();
        */

    }
}