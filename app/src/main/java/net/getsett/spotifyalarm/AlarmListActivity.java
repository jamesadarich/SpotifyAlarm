package net.getsett.spotifyalarm;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;


public class AlarmListActivity extends Activity {

    private List<Alarm> alarms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        alarms = new AlarmRepository().getAll();

        for(int i = 0; i < alarms.size(); i++){
            final Alarm alarm = alarms.get(i);
            Button button = new Button(this);
            button.setText(alarm.getTime().toString());
            final Context context = this;
            button.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){

                    Intent intent = new Intent(context, AlarmEditActivity.class);
                    intent.putExtra("AlarmTime", alarm.getTime());
                    intent.putExtra("AlarmLightFadeTimeInMinutes", alarm.getLightFadeTimeInMinutes());
                    startActivity(intent);
                }
            });

            ListView list = (ListView)findViewById(R.id.listView);
            list.addView(button);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarm_list, menu);
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

    public void createAlarm(View v) {

        Intent intent = new Intent(this, AlarmEditActivity.class);
        //puzzleIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
        //        + Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
    }
}
