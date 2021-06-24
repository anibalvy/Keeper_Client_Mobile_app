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

import static com.kanibalv.app.ServiceHandler.URL_GET_ROUTES;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link refreshRoutesFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class refreshRoutesFragment extends Fragment {

    Button btnRefreshRoute;

    public refreshRoutesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_refresh_routes, container, false);

        btnRefreshRoute = (Button) rootView.findViewById(R.id.btnRefreshRoute);

        btnRefreshRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetRoutes().execute();
            }
        });

        return rootView;
    }

    /*Async Class to refresh Routes*/
    public class GetRoutes extends AsyncTask<Void,Void,Void> {

        ProgressDialog pDialog = new ProgressDialog(getContext());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Fetching Routes information..");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            ServiceHandler jsonParser = new ServiceHandler();

            String json = jsonParser.makeServiceCall(URL_GET_ROUTES, ServiceHandler.GET);

            Log.d("GetRoutes Response: ", "> " + json);

            if (json != null) {
                try {
                    JSONObject mainJson = new JSONObject(json);
                    // database handler
                    SQL_Definition database = new SQL_Definition(getActivity().getApplicationContext());

                    if (mainJson != null) {
                        JSONArray rows = mainJson.getJSONArray("rows");


                        for (int i = 0; i < rows.length(); i++) {

                            JSONObject jsonObject = rows.getJSONObject(i);

                            String routeid = jsonObject.getString("routeid");
                            String routename = jsonObject.getString("routename");
                            String routedata = jsonObject.getString("routedata");
                            String routeenabled = jsonObject.getString("enabled");

                            Log.d("routeid: ", "> " + routeid);
                            Log.d("routename: ", "> " + routename);
                            Log.d("routedata: ", "> " + routedata);
                            Log.d("routeenabled: ", "> " + routeenabled);


                            HashMap<String, String> valueRouteTable = new HashMap<String, String>();
                            valueRouteTable.put("routeid",routeid);
                            valueRouteTable.put("routename",routename);
                            valueRouteTable.put("routedata",routedata);
                            valueRouteTable.put("routeenabled",routeenabled);

                            database.insertRoutes(valueRouteTable);
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
                pDialog.setMessage("Fetching Routes information.. OK");
            pDialog.dismiss();
            //populateSpinner();


        }
    }
}