package net.getsett.spotifyalarm.models;

import java.io.Serializable;

/**
 * Created by James on 09/05/2015.
 */
public class Options implements Serializable {

    public int TimeToSunset;
    public HueOptions HueOptions;
    public SpotifyOptions SpotifyOptions;

    public Options(){
        TimeToSunset = 5;
    }
}
