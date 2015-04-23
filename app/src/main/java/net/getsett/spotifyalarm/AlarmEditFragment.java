package net.getsett.spotifyalarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AlarmEditFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AlarmEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlarmEditFragment extends Fragment
implements View.OnClickListener {

    @Override
    public void onClick(View v) {
        if (getActivity().findViewById(R.id.button4) == v){

            Context context = getActivity().getApplicationContext();
            Intent intent = new Intent(context, AlarmBroadcastReceiver.class);


            SeekBar seekBar = (SeekBar)getActivity().findViewById(R.id.seekBar3);


            AlarmManager alarms ;
            alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            TimePicker time = (TimePicker)getActivity().findViewById(R.id.timePicker2);
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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlarmEditFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlarmEditFragment newInstance(String param1, String param2) {
        AlarmEditFragment fragment = new AlarmEditFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public AlarmEditFragment() {
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
        View view = inflater.inflate(R.layout.fragment_alarm_edit, container, false);

        view.findViewById(R.id.button4).setOnClickListener(this);

        SeekBar sk =(SeekBar) view.findViewById(R.id.seekBar3);

        final TextView seekBarValue = (TextView)view.findViewById(R.id.textView4);

        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                seekBarValue.setText(String.valueOf(progress) + " Minutes");
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
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

}
