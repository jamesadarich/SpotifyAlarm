package net.getsett.spotifyalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.SeekBar;

import com.orm.SugarRecord;

import java.util.UUID;
import java.util.Date;

/**
 * Created by James on 16/01/2015.
 */
public class Alarm extends SugarRecord<Alarm> {

    private int lightFadeTimeInMinutes;

    private Date time;

    public Alarm(){
    }

    public void set(Context context){
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);

        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        alarms.set(AlarmManager.RTC_WAKEUP, time.getTime(), pendingIntent);
    }

    public void unset(){
    }

    public Date getTime(){
        return time;
    }

    public void setTime(Date newTime){
        if (newTime.after(new Date())) {
            time = newTime;
        }
    }

    public int getLightFadeTimeInMinutes(){
        return lightFadeTimeInMinutes;
    }

    public void setLightFadeTimeInMinutes(int newLightFadeTimeInMinutes){
        if (newLightFadeTimeInMinutes > 0){
            lightFadeTimeInMinutes = newLightFadeTimeInMinutes;
        }
    }
}
