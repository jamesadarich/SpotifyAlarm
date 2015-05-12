package net.getsett.spotifyalarm.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import net.getsett.spotifyalarm.models.Options;
import net.getsett.spotifyalarm.services.SunriseService;

/**
 * Created by James on 15/01/2015.
 */
public class AlarmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent serviceIntent = new Intent(context, SunriseService.class);
        serviceIntent.putExtra("options", intent.getSerializableExtra("options"));
        context.startService(serviceIntent);
    }
}