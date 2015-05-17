package net.getsett.spotifyalarm.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import net.getsett.spotifyalarm.integrations.philipshue.HueLightBulb;
import net.getsett.spotifyalarm.integrations.spotify.SpotifyPlaylist;

/**
 * Created by James on 17/05/2015.
 */
public class HueLightBulbAdapter  extends ArrayAdapter<HueLightBulb> {

    public HueLightBulbAdapter(Context context, HueLightBulb[] lightBulbs) {
        super(context, 0, lightBulbs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HueLightBulb lightBulb = getItem(position);
        TextView textView = new TextView(getContext());
        textView.setTextColor(Color.BLACK);
        textView.setText(lightBulb.getName());
        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        HueLightBulb lightBulb = getItem(position);
        TextView textView = new TextView(getContext());
        textView.setTextColor(Color.BLACK);
        textView.setText(lightBulb.getName());
        return textView;
    }
}