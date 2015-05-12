package net.getsett.spotifyalarm.dataaccess;

import net.getsett.spotifyalarm.models.Alarm;

import java.util.List;

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
