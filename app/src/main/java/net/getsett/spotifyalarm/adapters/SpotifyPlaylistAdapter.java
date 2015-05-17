package net.getsett.spotifyalarm.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import net.getsett.spotifyalarm.integrations.spotify.SpotifyPlaylist;

/**
 * Created by James on 17/05/2015.
 */
public class SpotifyPlaylistAdapter extends ArrayAdapter<SpotifyPlaylist> {

    public SpotifyPlaylistAdapter(Context context, SpotifyPlaylist[] playlists) {
        super(context, 0, playlists);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SpotifyPlaylist playlist = getItem(position);
        TextView textView = new TextView(getContext());
        textView.setTextColor(Color.BLACK);
        textView.setText(playlist.Name);
        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        SpotifyPlaylist playlist = getItem(position);
        TextView textView = new TextView(getContext());
        textView.setTextColor(Color.BLACK);
        textView.setText(playlist.Name);
        return textView;
    }

}
