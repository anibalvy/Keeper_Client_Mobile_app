package com.kanibalv.app;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.kanibalv.app.ServiceHandler.URL_GET_VEHICLES;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RefreshVehiclesFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class RefreshVehiclesFragment extends Fragment {

    Button btnRefreshVehicles;

    public RefreshVehiclesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_refresh_vehicles, container, false);

        btnRefreshVehicles = (Button) rootView.findViewById(R.id.btnRefreshVehicles);

        btnRefreshVehicles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetVehicles().execute();
            }
        });


        return rootView;
    }

/*Async Class to refresh Users*/
public class GetVehicles extends AsyncTask<Void,Void,Void> {

    ProgressDialog pDialog = new ProgressDialog(getContext());
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //ProgressDialog pDialog = new ProgressDialog(refreshVehicles.this);
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
                SQL_Definition database = new SQL_Definition(getActivity().getApplicationContext());
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
        //ProgressDialog pDialog = new ProgressDialog(getActivity().getApplicationContext());
        if ( pDialog.isShowing())
            pDialog.setMessage("Fetching Vehicle information.. OK");
        pDialog.dismiss();
        //populateSpinner();


    }
}
}
