package com.kanibalv.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlaceholderFragmentNavigationView# newInstance} factory method to
 * create an instance of this fragment.
 */


public class PlaceholderFragmentNavigationView extends Fragment {

    private String sessionID;
    private String sessionStatus;
    private String userId;
    private String vehicleId;
    private String routeId;
    private String sessionDate;

    public PlaceholderFragmentNavigationView() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_navigation_view, container, false);

        String userName = "####";
        String vehicleIdentif = "####";
        String routeName = "####";
        String routeData = "####";


        // Get intent extras
        Intent intentNavigationView = getActivity().getIntent();
        userName       = intentNavigationView.getStringExtra(MainActivity.EXTRA_MESSAGE_USER);
        vehicleIdentif = intentNavigationView.getStringExtra(MainActivity.EXTRA_MESSAGE_IdentiVehicle);
        routeName      = intentNavigationView.getStringExtra(MainActivity.EXTRA_MESSAGE_ROUTE);

        // use this to start and trigger Navigation Service
        Intent intentNavigationService= new Intent(getActivity().getApplicationContext(),navigation_service.class);
        //intentNavigationService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // potentially add data to the intent
        //i.putExtra("KEY1", "Value to be used by the service");
        if( getActivity().startService(intentNavigationService) != null) {
            //Service is run
            Log.d("Ckecking Navigation Service status onCreate:", "Running");
        }else {
            //not running
            getActivity().startService(intentNavigationService);
            Log.d("Ckecking Navigation Service status onCreate:", "Started");
        }
        // Retain this fragment across configuration changes.
        //this.setRetainInstance(true);
        SQL_Definition database = new SQL_Definition(this.getActivity());
        HashMap<String, String> session = new HashMap<String, String>();
        session = database.getSessionActive();

        sessionID     = session.get(SQL_Definition.SESSION_ID);
        sessionStatus = session.get(SQL_Definition.SESSION_STATUS);
        userId        = session.get(SQL_Definition.USER_ID);
        vehicleId     = session.get(SQL_Definition.VEHICLE_ID);
        routeId       = session.get(SQL_Definition.ROUTE_ID);
        sessionDate   = session.get(SQL_Definition.CREATED_AT);

        HashMap<String, String> user = new HashMap<String, String>();
        user = database.getUserById(userId);
        userName = user.get(SQL_Definition.USER_NAME);


        HashMap<String, String> vehicle = new HashMap<String, String>();
        vehicle = database.getVehicleById(vehicleId);
        vehicleIdentif = vehicle.get(SQL_Definition.VEHICLE_IDENTIFIER);

        HashMap<String, String> route = new HashMap<String, String>();
        route = database.getRouteById(routeId);
        routeName = route.get(SQL_Definition.ROUTE_NAME);
        routeData = route.get(SQL_Definition.ROUTE_DATA);


        //Updating Session Status to Running
//            sessionStatus = "1";
//            session.remove(SQL_Definition.SESSION_STATUS);
//            session.put(SQL_Definition.SESSION_STATUS,sessionStatus);
//            database.updateSession(session);



        TextView displayUserSession = (TextView) rootView.findViewById(R.id.textValueUser);
        displayUserSession.setText(userName);

        TextView displayVehicleIdent = (TextView) rootView.findViewById(R.id.textValueVehicle);
        displayVehicleIdent.setText(vehicleIdentif);

        TextView displayRoute = (TextView) rootView.findViewById(R.id.textValueRoute);
        displayRoute.setText(routeName);

        TextView displayID = (TextView) rootView.findViewById(R.id.textValueSession);
        displayID.setText(sessionID);

        TextView displaySessionDate = (TextView) rootView.findViewById(R.id.textValueSessionDate);
        displaySessionDate.setText(sessionDate);

        return rootView;
    }


    //RECEIVER from Service to update data Values on the View
    public BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();

            if (bundle != null) {
                String recVelocity = intent.getStringExtra(navigation_service.SPEED);
                String recLongitude = intent.getStringExtra(navigation_service.LONGITUDE);
                String recLatitude = intent.getStringExtra(navigation_service.LATITUDE);
                String recStatus = intent.getStringExtra(navigation_service.STATUS);
                String recSync = intent.getStringExtra(navigation_service.SYNCCOUNT);
                String recCheckPoint = intent.getStringExtra(navigation_service.CHECKPOINT);

                TextView displaySpeed = (TextView) getView().findViewById(R.id.textValueVelocity);
                //displaySpeed.setTextSize(40);
//                displaySpeed.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40f);
//                displaySpeed.setTextColor(Color.DKGRAY);
                displaySpeed.setText(recVelocity);

                TextView displayLongitude = (TextView)  getView().findViewById(R.id.textValueLongitude);
                displayLongitude.setText(recLongitude);

                TextView displayLatitude = (TextView)  getView().findViewById(R.id.textValueLatitude);
                displayLatitude.setText(recLatitude);

                TextView displayStatus = (TextView)  getView().findViewById(R.id.textValueStatus);
                displayStatus.setText(recStatus);

                TextView displaySync = (TextView)  getView().findViewById(R.id.textValueSync);
                displaySync.setText(recSync);

                if (recSync.equals("Synchronized")) {
                    displaySync.setBackgroundColor(getResources().getColor(R.color.green));
                    //displaySync.setBackgroundColor(getColor(R.color.green));
                } else {
                    displaySync.setBackgroundColor(getResources().getColor(R.color.grey_default));
                    //displaySync.setBackgroundColor(getColor(R.color.grey_default));
                }
                ;

                TextView displayCheckPoint = (TextView)  getView().findViewById(R.id.textValueCheckPoint);
                displayCheckPoint.setText(recCheckPoint);
            }
        };
    };

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver, new IntentFilter(navigation_service.NOTIFICATION));


        // use this to start and trigger Navigation Service
        //Intent intentNavigationService= new Intent(this, navigation_service.class);
        Intent intentNavigationService= new Intent(getActivity(), navigation_service.class);
        //intentNavigationService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // potentially add data to the intent
        //i.putExtra("KEY1", "Value to be used by the service");


        if(getActivity().startService(intentNavigationService) != null) {
            //Service is run
            Log.d("Ckecking Navigation Service status onResume:", "Running");
        }else {
            //not running
            getActivity().startService(intentNavigationService);
            Log.d("Ckecking Navigation Service status onResume:", "Started");
        }
    }
}

















