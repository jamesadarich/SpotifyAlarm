package net.getsett.spotifyalarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;


public class AlarmEditActivity extends Activity {

    private int brightness = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_edit);

        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);
        final TextView seekBarValue = (TextView)findViewById(R.id.textView);

        int lightFadeTimeInMinutes = this.getIntent().getIntExtra("AlarmLightFadeTimeInMinutes", 0);
        if (lightFadeTimeInMinutes > 0){
            seekBar.setProgress(lightFadeTimeInMinutes);
        }

        Date time = (Date)this.getIntent().getSerializableExtra("AlarmTime");
        if (time != null){
            TimePicker picker = (TimePicker)findViewById(R.id.timePicker);
            picker.setCurrentHour(time.getHours());
            picker.setCurrentMinute(time.getMinutes());
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                seekBarValue.setText(String.valueOf(progress) + " minutes");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarms, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void test(View v) {
        Context context = getApplicationContext();
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);


        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);


        AlarmManager alarms ;
        alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        TimePicker time = (TimePicker)findViewById(R.id.timePicker);
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, time.getCurrentHour());
        calendar.set(Calendar.MINUTE, time.getCurrentMinute());
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTime().before(new Date())){
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        }

        Alarm alarm = new Alarm();
        alarm.setLightFadeTimeInMinutes(seekBar.getProgress());
        alarm.setTime(calendar.getTime());

        new AlarmRepository().add(alarm);

        intent.putExtra("AlarmId", alarm.getId());
        intent.putExtra("Minutes", seekBar.getProgress());

        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        alarms.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
    }
}
