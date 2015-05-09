package net.getsett.spotifyalarm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.PowerManager;
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

import org.acra.ACRA;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GoToSleepFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GoToSleepFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class GoToSleepFragment extends Fragment
        implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (getActivity().findViewById(R.id.switch1) == buttonView) {
            getActivity().findViewById(R.id.spinner).setEnabled(isChecked);
        }
        else if (getActivity().findViewById(R.id.switch2) == buttonView) {
            getActivity().findViewById(R.id.spinner3).setEnabled(isChecked);
        }
        //whatever you want
    }

    @Override
    public void onClick(View v) {
        if (getActivity().findViewById(R.id.button5) == v){

            /*Intent puzzleIntent = new Intent(getActivity(), GoToSleepActivity.class);

            puzzleIntent.putExtra("Minutes", ((SeekBar)getActivity().findViewById(R.id.seekBar4)).getProgress());

            puzzleIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    + Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            getActivity().startActivity(puzzleIntent);
            */

            Intent intent = new Intent(getActivity(), SunsetService.class);
            intent.putExtra("Minutes", ((SeekBar)getActivity().findViewById(R.id.seekBar4)).getProgress());
            if (((Switch)getActivity().findViewById(R.id.switch1)).isChecked()) {
                intent.putExtra("SpotifyToken", _spotifyToken);
                Spinner s = (Spinner) getActivity().findViewById(R.id.spinner);
                try {
                    intent.putExtra("SpotifyUri", _spotifyPlaylists.getJSONObject((int) s.getSelectedItemId()).get("uri").toString());
                } catch (JSONException exception) {
                    ACRA.getErrorReporter().handleSilentException(exception);
                }
            }

            if (((Switch)getActivity().findViewById(R.id.switch2)).isChecked()) {
                Spinner s = (Spinner) getActivity().findViewById(R.id.spinner3);

                intent.putExtra("HueLightId", _lights.get(s.getSelectedItem().toString()).toString());
            }


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
    public static GoToSleepFragment newInstance(String param1, String param2) {
        GoToSleepFragment fragment = new GoToSleepFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public GoToSleepFragment() {
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
        View view = inflater.inflate(R.layout.fragment_go_to_sleep, container, false);

        view.findViewById(R.id.button5).setOnClickListener(this);
        ((Switch)view.findViewById(R.id.switch1)).setOnCheckedChangeListener(this);
        ((Switch)view.findViewById(R.id.switch2)).setOnCheckedChangeListener(this);

        SeekBar sk =(SeekBar) view.findViewById(R.id.seekBar4);

        final TextView seekBarValue = (TextView)view.findViewById(R.id.textView5);

        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

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

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private JSONArray _spotifyPlaylists;

    // TODO: Replace with your redirect URI
    private static final String REDIRECT_URI = "spotify-alarm://callback";
    private String _spotifyToken = "";

    // Request code that will be passed together with authentication result to the onAuthenticationResult callback
    // Can be any integer
    private static final int REQUEST_CODE = 1337;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {

                _spotifyToken = response.getAccessToken();

                getUsername();
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(getString(R.string.spotifyApiKey), AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        Intent intent = AuthenticationClient.createLoginActivityIntent(getActivity(), request);
        startActivityForResult(intent, REQUEST_CODE);

        // To close LoginActivity
        AuthenticationClient.stopLoginActivity(getActivity(), REQUEST_CODE);

        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        JsonArrayRequest hueRequest = new JsonArrayRequest("https://www.meethue.com/api/nupnp",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {

                            String _hueIpAddress = ((JSONObject) response.get(0)).get("internalipaddress").toString();
                            String _hueUsername = "james-test";
                            JsonObjectRequest lightsRequest = new JsonObjectRequest(Request.Method.GET,
                                    "http://" + _hueIpAddress + "/api/" + _hueUsername + "/lights",
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            List<String> lightNames = new ArrayList<String>();

                                            Iterator<String> keys = response.keys();
                                            while(keys.hasNext()) {
                                                String key = keys.next();

                                                try {
                                                    String name = response.getJSONObject(key).getString("name");
                                                    _lights.put(name, Integer.parseInt(key));
                                                    lightNames.add(name);
                                                }
                                                catch (JSONException ex){

                                                }
                                            }
                                            String[] lightNamesArray = new String[lightNames.size()];
                                            lightNamesArray = lightNames.toArray(lightNamesArray);

                                            Spinner s = (Spinner) getActivity().findViewById(R.id.spinner3);
                                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                                                    android.R.layout.simple_spinner_item, lightNamesArray);
                                            s.setAdapter(adapter);
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    ACRA.getErrorReporter().handleSilentException(error);
                                    //handler.postDelayed(decrementLight, lightInterval);
                                }
                            });

                            queue.add(lightsRequest);
                            //handler.post(decrementLight);
                        } catch (JSONException e) {

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ACRA.getErrorReporter().handleSilentException(error);
            }
        });

        queue.add(hueRequest);
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

    private void getUsername(){
        //Get the users username
        JsonObjectRequest usernameRequest = new SpotifyWebApiRequest(
                Request.Method.GET,
                "https://api.spotify.com/v1/me",
                null,
                _spotifyToken,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        //Log.d("Response", response.toString());

                        try {
                            getUserPlaylists(response.get("id").toString());
                        }
                        catch (JSONException exception){
                            ACRA.getErrorReporter().handleSilentException(exception);
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ACRA.getErrorReporter().handleSilentException(error);
                    }
                }
        );

        queue.add(usernameRequest);
    }

    private void getUserPlaylists(String username){
        //Get the users username
        JsonObjectRequest playlistRequest = new SpotifyWebApiRequest(
                Request.Method.GET,
                "https://api.spotify.com/v1/users/" + username + "/playlists",
                null,
                _spotifyToken,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        try {
                            _spotifyPlaylists = response.getJSONArray("items");

                            List<String> playlistNames = new ArrayList<String>();
                            for(int i = 0; i < _spotifyPlaylists.length(); i++){
                                playlistNames.add(_spotifyPlaylists.getJSONObject(i).get("name").toString());
                            }
                            String[] playlistNamesArray = new String[playlistNames.size()];
                            playlistNamesArray = playlistNames.toArray(playlistNamesArray);

                            Spinner s = (Spinner) getActivity().findViewById(R.id.spinner);
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                                    android.R.layout.simple_spinner_item, playlistNamesArray);
                            s.setAdapter(adapter);
                        }
                        catch (JSONException exception)
                        {
                            ACRA.getErrorReporter().handleSilentException(exception);
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ACRA.getErrorReporter().handleSilentException(error);
                    }
                }
        );
        queue.add(playlistRequest);
    }
}
