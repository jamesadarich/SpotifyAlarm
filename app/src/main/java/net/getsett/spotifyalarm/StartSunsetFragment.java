package net.getsett.spotifyalarm;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import net.getsett.spotifyalarm.adapters.HueLightBulbAdapter;
import net.getsett.spotifyalarm.adapters.SpotifyPlaylistAdapter;
import net.getsett.spotifyalarm.integrations.philipshue.HueBridge;
import net.getsett.spotifyalarm.integrations.philipshue.HueLightBulb;
import net.getsett.spotifyalarm.integrations.spotify.SpotifyPlaylist;
import net.getsett.spotifyalarm.integrations.spotify.SpotifyToken;
import net.getsett.spotifyalarm.integrations.spotify.SpotifyTokenGenerator;
import net.getsett.spotifyalarm.integrations.spotify.SpotifyUser;
import net.getsett.spotifyalarm.integrations.spotify.SpotifyWebApiRequest;
import net.getsett.spotifyalarm.models.HueOptions;
import net.getsett.spotifyalarm.models.Options;
import net.getsett.spotifyalarm.models.SpotifyOptions;
import net.getsett.spotifyalarm.services.SunsetService;

import org.acra.ACRA;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StartSunsetFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StartSunsetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class StartSunsetFragment extends Fragment
        implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (getActivity().findViewById(R.id.playlistSwitch) == buttonView) {
            getActivity().findViewById(R.id.sunsetPlaylistSelect).setEnabled(isChecked);
            getActivity().findViewById(R.id.playlistRandomise).setEnabled(isChecked);
        }
        else if (getActivity().findViewById(R.id.lightSwitch) == buttonView) {
            getActivity().findViewById(R.id.lightSelect).setEnabled(isChecked);
        }
        //whatever you want
    }

    @Override
    public void onClick(View v) {
        if (getActivity().findViewById(R.id.sunsetStartButton) == v){

            Options options = new Options();
            options.TimeToSunset = ((SeekBar)getActivity().findViewById(R.id.easePeriod)).getProgress() + 1;

            //If audio is requested by the user then get the details
            if (((Switch)getActivity().findViewById(R.id.playlistSwitch)).isChecked()) {

                options.SpotifyOptions = new SpotifyOptions();
                options.SpotifyOptions.Token = _spotifyToken.toString();
                //options.SpotifyOptions.RefreshToken = _spotifyToken.getRefreshToken();
                options.SpotifyOptions.Randomise = ((Switch)getActivity().findViewById(R.id.playlistRandomise)).isChecked();

                Spinner s = (Spinner) getActivity().findViewById(R.id.sunsetPlaylistSelect);
                options.SpotifyOptions.PlaylistUri = _spotifyPlaylists[(int)s.getSelectedItemId()].Uri;
            }

            if (((Switch)getActivity().findViewById(R.id.lightSwitch)).isChecked()) {

                options.HueOptions = new HueOptions();

                Spinner s = (Spinner) getActivity().findViewById(R.id.lightSelect);

                options.HueOptions.LightBulbId =  ((HueLightBulb)s.getSelectedItem()).getId();
            }

            //Create new intent add the info and start the service!
            Intent intent = new Intent(getActivity(), SunsetService.class);
            intent.putExtra("options", options);
            getActivity().startService(intent);
        }
    }
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private RequestQueue queue;

    private Map<String, Integer> _lights = new HashMap<String, Integer>();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GoToSleepFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StartSunsetFragment newInstance(String param1, String param2) {
        StartSunsetFragment fragment = new StartSunsetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public StartSunsetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_start_sunset, container, false);

        view.findViewById(R.id.sunsetStartButton).setOnClickListener(this);
        ((Switch)view.findViewById(R.id.lightSwitch)).setOnCheckedChangeListener(this);
        ((Switch)view.findViewById(R.id.playlistSwitch)).setOnCheckedChangeListener(this);

        SeekBar sk =(SeekBar) view.findViewById(R.id.easePeriod);

        final TextView seekBarValue = (TextView)view.findViewById(R.id.easePeriodText);

        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                String seekBarText = String.valueOf(progress + 1) + " minute";
                if (progress != 1){
                    seekBarText += "s";
                }
                seekBarValue.setText(seekBarText);
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

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private SpotifyPlaylist[] _spotifyPlaylists;

    // TODO: Replace with your redirect URI
    private static final String REDIRECT_URI = "spotify-alarm://callback";
    private SpotifyToken _spotifyToken;
    private String _spotifyCode = "";

    // Request code that will be passed together with authentication result to the onAuthenticationResult callback
    // Can be any integer
    private static final int REQUEST_CODE = 1337;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.CODE) {

                _spotifyCode = response.getCode();

                new SetupPlaylistsTask().execute();
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(getString(R.string.spotifyApiKey), AuthenticationResponse.Type.CODE, REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        Intent intent = AuthenticationClient.createLoginActivityIntent(getActivity(), request);
        startActivityForResult(intent, REQUEST_CODE);

        // To close LoginAct-ivity
        AuthenticationClient.stopLoginActivity(getActivity(), REQUEST_CODE);

        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        new SetupLightsTask().execute();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private class SetupPlaylistsTask extends AsyncTask<Void, Void, List<SpotifyPlaylist>> {
        /** The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute() */
        protected List<SpotifyPlaylist> doInBackground(Void... nothing) {
            //loadImageFromNetwork(urls[0]);

            SpotifyTokenGenerator spotifyTokenGenerator = new SpotifyTokenGenerator(getActivity());
            _spotifyToken = spotifyTokenGenerator.getToken(_spotifyCode);
            SpotifyUser user = new SpotifyUser(_spotifyToken, getActivity());
            return user.getPlaylists();
        }

        protected void onProgressUpdate(Void... progress) {
        }

        /** The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground() */
        protected void onPostExecute(List<SpotifyPlaylist> playlists) {
            //mImageView.setImageBitmap(result);
            // When the loop is finished, updates the notification
            Spinner s = (Spinner) getActivity().findViewById(R.id.sunsetPlaylistSelect);
            _spotifyPlaylists = new SpotifyPlaylist[playlists.size()];
            _spotifyPlaylists = playlists.toArray(_spotifyPlaylists);
            SpotifyPlaylistAdapter adapter = new SpotifyPlaylistAdapter(
                    getActivity(),
                    _spotifyPlaylists
            );
            s.setAdapter(adapter);
        }
    }

    private class SetupLightsTask extends AsyncTask<Void, Void, List<HueLightBulb>> {
        /** The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute() */
        protected List<HueLightBulb> doInBackground(Void... nothing) {
            //loadImageFromNetwork(urls[0]);
            HueBridge bridge = new HueBridge(getActivity());

            return bridge.getAllLightBulbs();
        }

        protected void onProgressUpdate(Void... progress) {
        }

        /** The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground() */
        protected void onPostExecute(List<HueLightBulb> lightBulbs) {
            //mImageView.setImageBitmap(result);
            // When the loop is finished, updates the notification

            Spinner s = (Spinner) getActivity().findViewById(R.id.lightSelect);
            HueLightBulb[] lightsArray = new HueLightBulb[lightBulbs.size()];
            lightsArray = lightBulbs.toArray(lightsArray);
            HueLightBulbAdapter adapter = new HueLightBulbAdapter(
                    getActivity(),
                    lightsArray);
            s.setAdapter(adapter);
        }
    }
}
