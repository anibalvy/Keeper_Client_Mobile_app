package com.kanibalv.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class new_vehicle extends AppCompatActivity {

    ProgressDialog pDialog;

    // API urls
    // Url to WS
    private String URL_SERVER = "http://kanibal.servebeer.com:3000/";
    //private String URL_SERVER = "http://172.31.110.195:3000/";
    private String URL_GET_VEHICLES  = URL_SERVER + "getVehicles";
    private String URL_POST_VEHICLES = URL_SERVER + "postVehicle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_vehicle);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

// Action Bar Disabled (Commented)
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.new_vehicle, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will172.31.110.195
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public  class PlaceholderFragment extends Fragment {

        Button btnNewVehicle;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_new_vehicle, container, false);


            //Button action
            // Save New User button
            btnNewVehicle = (Button) rootView.findViewById(R.id.btAddVehicle);

            final EditText newVehicle = (EditText) rootView.findViewById(R.id.editTextAddVehicle);

            /**
             * Add new label button click listener
             * */
            btnNewVehicle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String newVehicleValue =  newVehicle.getText().toString();
                    boolean isConnectedToServerValue = false;
                    //String user   = spinnerChooseUser.getSelectedItem().toString();

                    isConnectedToServer chkConn = new isConnectedToServer();
                    try {
                        isConnectedToServerValue = chkConn.execute(URL_SERVER).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    if (isOnline()) {
                        if (isConnectedToServerValue){
                            if (newVehicleValue.trim().length() > 0) {


                                //HashMap<String, String> valueVehicleTable = new HashMap<String, String>();

                                // database handler
                                //SQL_Definition database = new SQL_Definition(getActivity().getApplicationContext());

                                //valueVehicleTable.put("vehicleIdentifier",newVehicleValue);

                                //database.insertVehicle(valueVehicleTable);
                                new PostVehicle().execute(newVehicleValue);

                                Toast.makeText(v.getContext(), "Inserting New Vehicle " + newVehicleValue,
                                        Toast.LENGTH_SHORT).show();
                                newVehicle.setText("");

                            } else {
                                Toast.makeText(v.getContext(), "Please enter New Vehicle identification",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(v.getContext(), "Server is not available",
                                    Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(v.getContext(), "Network is not available",
                                Toast.LENGTH_SHORT).show();
                    }


                }
            });

            return rootView;
        }
    }


    /*Async Class to Add New User*/
    public class PostVehicle extends AsyncTask<String, Void, Void> {

        boolean isNewVehicleCreated = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(new_vehicle.this);
            pDialog.setMessage("Adding new Vehicle..");
            pDialog.setCancelable(false);
            pDialog.show();
            Log.d("llamando a PostVehicle", "sincronizando");
        }
        @Override
        protected Void doInBackground(String... arg) {

            String newVehicleValue = arg[0];


//            // // database handler
//            SQL_Definition database = new SQL_Definition(getApplicationContext());
//            String rowLimit = "10";
//            List<String> tracks = database.getTracksForSync(rowLimit);

            Log.d("newVehicleValue","newVehicleValue: " + newVehicleValue);

            if (newVehicleValue.trim().length() > 0) {

//                String dataTracks = "[";
//                for (int i = 0; i < tracks.size(); i++) {
//                    dataTracks = dataTracks + tracks.get(i) +",";
//                }
//                dataTracks = dataTracks.substring(0, dataTracks.length()-1) + "]";

                // Preparing post params
                List<NameValuePair> params = new ArrayList<NameValuePair>();
//                Log.d("trackdata",dataTracks);

                params.add(new BasicNameValuePair("dataVehicle", newVehicleValue));
                //            params.add(new BasicNameValuePair("name2", "NUEVO VALOR2"));

                ServiceHandler serviceClient = new ServiceHandler();

                Log.d("llamando a PostTrack WS", "WSWSWSWS");
                String json = serviceClient.makeServiceCall(URL_POST_VEHICLES,ServiceHandler.POST, params);

                Log.d("Create Vehicle Response: ", "> " + json);

                if (json != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(json);
                        boolean error = jsonObj.getBoolean("error");

                        // checking for error node in json
                        if (!error) {
                            // new category created successfully
                            isNewVehicleCreated = true;

                        } else {
                            //Log.e("Create Category Error: ", "> " + jsonObj.getString("message"));
                            Log.e("Create User Error: ", "> " );
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("JSON Data", "Didn't receive any data from server!");
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            if (isNewVehicleCreated) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // fetching all users
                        new GetVehicles().execute();
                    }
                });
            }
        }
    }

    /*Async Class to refresh Users*/
    public class GetVehicles extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(new_vehicle.this);
            pDialog.setMessage("Fetching Vehicles information..");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {

            ServiceHandler jsonParser = new ServiceHandler();

            String json = jsonParser.makeServiceCall(URL_GET_VEHICLES, ServiceHandler.GET);

            Log.d("GetVehicles Response: ", "> " + json);

            if (json != null) {
                try {
                    JSONObject mainJson = new JSONObject(json);
                    // database handler
                    SQL_Definition database = new SQL_Definition(getApplicationContext());
                    if (mainJson != null) {
                        JSONArray rows = mainJson.getJSONArray("rows");
                        Log.d("Vehicles Rows", rows.toString());

                        for (int i = 0; i < rows.length(); i++) {

                            JSONObject jsonObject = rows.getJSONObject(i);

                            String vehicleid = jsonObject.getString("vehicleid");
                            String vehicleidenti = jsonObject.getString("vehicleidenti");

                            Log.d("vehicleid: ", "> " + vehicleid);
                            Log.d("vehicleidenti: ", "> " + vehicleidenti);


                            HashMap<String, String> valueVehicleTable = new HashMap<String, String>();



                            valueVehicleTable.put(SQL_Definition.VEHICLE_ID,vehicleid);
                            valueVehicleTable.put(SQL_Definition.VEHICLE_IDENTIFIER,vehicleidenti);

                            database.insertVehicle(valueVehicleTable);

                        }
                    }
                    database.close();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("JSON Data", "Didn't receive any data from server!");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.setMessage("Fetching Vehicle information.. OK");
            pDialog.dismiss();
            //populateSpinner();


        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public class isConnectedToServer extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... urls) {

            try{
                URL myUrl = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                connection.setConnectTimeout(5000);
                connection.connect();
                return true;
            } catch (Exception e) {
                // Handle your exceptions
                Log.d("Server error", e.toString());
                return false;
            }
            //return null;
        }

        @Override
        protected void onPostExecute(Boolean result){

            return;
        }
    }

}
