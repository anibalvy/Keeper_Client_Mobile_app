package com.kanibalv.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlaceholderFragmentNavigationView#newInstance} factory method to
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

        // Retain this fragment across configuration changes.
        //this.setRetainInstance(true);

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
}


