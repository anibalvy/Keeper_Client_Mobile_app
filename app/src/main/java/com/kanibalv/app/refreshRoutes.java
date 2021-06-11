package com.kanibalv.app;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class refreshRoutes extends AppCompatActivity {

    ProgressDialog pDialog;

    // API urls
    // Url to get all Routes
    private String URL_SERVER = "http://kanibal.servebeer.com:3000/";
    //private String URL_SERVER = "http://172.31.110.195:3000/";
    private String URL_GET_ROUTES = URL_SERVER + "getRoutes";
    //private String URL_GET_ROUTES = "http://172.31.110.195:3000/getRoutes";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh_routes);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

// Action Bar Disabled (Commented) -- AVY
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.refresh_routes, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
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

        Button btnRefreshRoute;

        public PlaceholderFragment() {
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
    }

    /*Async Class to refresh Routes*/
    public class GetRoutes extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(refreshRoutes.this);
            pDialog.setMessage("Fetching Routes information..");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {

            ServiceHandler jsonParser = new ServiceHandler();

            String json = jsonParser.makeServiceCall(URL_GET_ROUTES, ServiceHandler.GET);

            Log.d("Response: ", "> " + json);

            if (json != null) {
                try {
                    JSONObject mainJson = new JSONObject(json);
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

                            // database handler
                            SQL_Definition database = new SQL_Definition(getApplicationContext());

                            valueRouteTable.put("routeid",routeid);
                            valueRouteTable.put("routename",routename);
                            valueRouteTable.put("routedata",routedata);
                            valueRouteTable.put("routeenabled",routeenabled);


                            database.insertRoutes(valueRouteTable);




                        }
                    }

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
