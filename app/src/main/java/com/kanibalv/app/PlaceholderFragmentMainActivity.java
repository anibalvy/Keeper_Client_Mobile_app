package com.kanibalv.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.kanibalv.app.MainActivity.EXTRA_MESSAGE_IdentiVehicle;
import static com.kanibalv.app.MainActivity.EXTRA_MESSAGE_ROUTE;
import static com.kanibalv.app.MainActivity.EXTRA_MESSAGE_USER;
import static com.kanibalv.app.MainActivity.IMEI;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlaceholderFragmentMainActivity#newInstance} factory method to
 * create an instance of this fragment.
 */
/**
 public class PlaceholderFragmentMainActivity extends Fragment {
     * A placeholder fragment containing a simple view.
     */
public class PlaceholderFragmentMainActivity extends Fragment implements TextToSpeech.OnInitListener {

        // Spinner element
        public Spinner spinnerChooseUser;
        public Spinner spinnerChooseVehicle;
        public Spinner spinnerChooseRoute;

        public boolean isSpinnerChooseUserSelect = false;
        public boolean isSpinnerChooseVehicleSelect = false;
        public boolean isSpinnerChooseRouteSelect = false;

        // Add button
        Button btnStartSession;

        // Text to Speech
        private TextToSpeech talker;
        private boolean statusOfGPS;


        public PlaceholderFragmentMainActivity() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Retain this fragment across configuration changes.
            this.setRetainInstance(true);

            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            // Loading spinner data from database
            loadSpinnerUsersData(rootView);
            loadSpinnerVehiclesData(rootView);
            loadSpinnerRoutesData(rootView);


            spinnerChooseVehicle.setPrompt("Select a Vehicle");
            spinnerChooseRoute.setPrompt("Select a Route");

            // spinner item select listener
            //spinnerChooseUser.setOnItemSelectedListener(on);
            spinnerChooseUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // On selecting a spinner item
                    String label = parent.getItemAtPosition(position).toString();

                    // Showing selected spinner item
                    //Toast.makeText(parent.getContext(), "You selected User: " + label,Toast.LENGTH_LONG).show();

                    isSpinnerChooseUserSelect = true;

                    if (isSpinnerChooseUserSelect&&isSpinnerChooseVehicleSelect&&isSpinnerChooseRouteSelect) {
                        //btnStartSession.setVisibility(View.VISIBLE);
                        btnStartSession.setEnabled(true);
                    }

                    spinnerChooseUser.setPrompt("Select a User");

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            // spinner item select listener
            spinnerChooseVehicle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // On selecting a spinner item
                    String label = parent.getItemAtPosition(position).toString();

                    // Showing selected spinner item
                    //Toast.makeText(parent.getContext(), "You selected Vehicle: " + label,Toast.LENGTH_LONG).show();

                    isSpinnerChooseVehicleSelect = true;

                    if (isSpinnerChooseUserSelect&&isSpinnerChooseVehicleSelect&&isSpinnerChooseRouteSelect) {
                        //btnStartSession.setVisibility(View.VISIBLE);
                        btnStartSession.setEnabled(true);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            // spinner item select listener


            spinnerChooseRoute.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // On selecting a spinner item
                    String labelRoute = parent.getItemAtPosition(position).toString();

                    // Showing selected spinner item
                    //Toast.makeText(parent.getContext(), "You selected Route: " + labelRoute,Toast.LENGTH_LONG).show();

                    isSpinnerChooseRouteSelect = true;

                    if (isSpinnerChooseUserSelect&&isSpinnerChooseVehicleSelect&&isSpinnerChooseRouteSelect) {
                        //btnStartSession.setVisibility(View.VISIBLE);
                        btnStartSession.setEnabled(true);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            // Displaying GPS Warning
            TextView displayGpsState = (TextView) rootView.findViewById(R.id.gpsStatus);
            if (!statusOfGPS){
                displayGpsState.setText("GPS is Off, pleaseTurn ON");
                displayGpsState.setTextColor(Color.RED);
            }
            else {
                displayGpsState.setText("");
            };


            //Button action
            // Start Session button
            btnStartSession = (Button) rootView.findViewById(R.id.buttonStartSession);
            //btnStartSession.setVisibility(View.INVISIBLE);
            btnStartSession.setEnabled(false);

            /**
             * Add new label button click listener
             * */
            btnStartSession.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    String userSelected    = spinnerChooseUser.getSelectedItem().toString();
                    String vehicleSelected = spinnerChooseVehicle.getSelectedItem().toString();
                    String routeSelected   = spinnerChooseRoute.getSelectedItem().toString();
                    String userId    = "";
                    String vehicleId = "";
                    String routeId   = "";
                    String routeData   = "";
                    String status    = "0";

                    if ((userSelected.trim().length() > 0) && (vehicleSelected.trim().length() >0) && (routeSelected.trim().length() >0)){

                        // Open a database for reading only
                        // database handler
                        SQL_Definition database = new SQL_Definition(getActivity().getApplicationContext());
                        userId = database.getUserIdByName(userSelected);
                        vehicleId = database.getVehicleIdByName(vehicleSelected);
                        routeId = database.getRouteIdByName(routeSelected);

                        HashMap<String, String> route = new HashMap<String, String>();
                        route = database.getRouteById(routeId);
                        routeData = route.get(SQL_Definition.ROUTE_DATA);


                        Toast.makeText(arg0.getContext(), "Selected: \nUser " + userSelected + " with ID " + userId
                                        + "\nVehicle " + vehicleSelected + " with ID " + vehicleId
                                        + "\nRoute " + routeSelected + " with ID " + routeId
                                ,Toast.LENGTH_SHORT).show();

                        //Save Session Info.
                        HashMap<String, String> valueSessionTable = new HashMap<String, String>();

                        status = "1";
                        valueSessionTable.put(SQL_Definition.SESSION_STATUS,status);
                        valueSessionTable.put(SQL_Definition.IMEI, IMEI);
                        valueSessionTable.put(SQL_Definition.USER_ID,userId);
                        valueSessionTable.put(SQL_Definition.VEHICLE_ID,vehicleId);
                        valueSessionTable.put(SQL_Definition.ROUTE_ID,routeId);
                        valueSessionTable.put(SQL_Definition.ROUTE_NAVIGATION,routeData);

                        database.insertSession(valueSessionTable);

                        database.close();

                        // Call Navigation View
                        Intent intentNavigationView = new Intent(getActivity(), navigation_view.class);
                        intentNavigationView.putExtra(EXTRA_MESSAGE_USER,userSelected);
                        intentNavigationView.putExtra(EXTRA_MESSAGE_IdentiVehicle,vehicleSelected);
                        intentNavigationView.putExtra(EXTRA_MESSAGE_ROUTE,routeSelected);
                        startActivity(intentNavigationView);


                    } else {
                        Toast.makeText(arg0.getContext(), "Error > Selected: \nUser " + userSelected + " with ID " + userId + "\nVehicle " + vehicleSelected + "\nRoute " + routeSelected,
                                Toast.LENGTH_SHORT).show();
                    }

                }
            });

//            talker = new TextToSpeech(getApplicationContext(), this);
//            talker.speak("Navegación iniciada", TextToSpeech.QUEUE_FLUSH, null);

            return rootView;
        }


        //@Override
        public void onResume(View view) {

            // Loading spinner data from database
            loadSpinnerUsersData(view);
            loadSpinnerVehiclesData(view);
            loadSpinnerRoutesData(view);

        }

        /**
         * Function to load the spinner Users data from SQLite database
         * */
        public void loadSpinnerUsersData(View view) {

            // Spinner element
            spinnerChooseUser = (Spinner) view.findViewById(R.id.spinnerChooseUser);

            // database handler
            SQL_Definition database = new SQL_Definition(getActivity().getApplicationContext());

            // Spinner Drop down elements
            List<String> usersLabels = database.getUsersLabels();
            database.close();

            // Creating adapter for spinner
            ArrayAdapter<String> dataAdapter;
            dataAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, usersLabels);


            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // attaching data adapter to spinner
            spinnerChooseUser.setAdapter(dataAdapter);
        }

        /**
         * Function to load the spinner Users data from SQLite database
         * */
        private void loadSpinnerVehiclesData( View view ) {
            // Spinner element
            spinnerChooseVehicle = (Spinner) view.findViewById(R.id.spinnerChooseVehicle);


            // database handler
            SQL_Definition database = new SQL_Definition(getActivity().getApplicationContext());

            // Spinner Drop down elements
            List<String> vehiclesLabels = database.getVehiclesLabels();

            database.close();

            // Creating adapter for spinner
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_dropdown_item, vehiclesLabels);

            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // attaching data adapter to spinner
            spinnerChooseVehicle.setAdapter(dataAdapter );
        }

        /**
         * Function to load the spinner Users data from SQLite database
         * */
        private void loadSpinnerRoutesData(View view) {

            // Spinner element
            spinnerChooseRoute   = (Spinner) view.findViewById(R.id.spinnerChooseRoute);

            // database handler
            //SQL_Definition database = new SQL_Definition(getApplicationContext());
            SQL_Definition database = new SQL_Definition(getContext());

            // Spinner Drop down elements
            List<String> routesLabels = database.getRoutesLabels();

            database.close();

            // Creating adapter for spinner
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, routesLabels);

            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // attaching data adapter to spinner
            spinnerChooseRoute.setAdapter(dataAdapter);
        }

        //Talker onInit method
        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {

                Log.d("TTS", "Initialization OK!");

                //int result = talker.setLanguage(Locale.ES);
                Locale locSpanish = new Locale("spa", "CL");
                int result = talker.setLanguage(locSpanish);

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "This Language is not supported");
                } else {
                    String text = getResources().getString(R.string.ttsInitSession);
                    //talker.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                }

            } else {
                Log.e("TTS", "Initialization Failed!");
            }

        }
    }
