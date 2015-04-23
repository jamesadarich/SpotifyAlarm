package net.getsett.spotifyalarm;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

/**
 * Created by James on 16/01/2015.
 */
public class AlarmRepository {

    public Alarm get(long id){
        return Alarm.findById(Alarm.class, id);
    }

    public List<Alarm> getAll(){
        return Alarm.listAll(Alarm.class);
    }

    public void add(Alarm alarm){
        alarm.save();
    }

    public void delete(long alarmId){
        Alarm.findById(Alarm.class, alarmId).delete();
    }
}
