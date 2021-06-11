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

import static com.kanibalv.app.ServiceHandler.URL_GET_USERS;
import static com.kanibalv.app.ServiceHandler.URL_POST_USERS;
import static com.kanibalv.app.ServiceHandler.URL_SERVER;

public class new_userFragment extends Fragment {

    static ProgressDialog pDialog;

    Button btnNewUser;

    public new_userFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_user, container, false);

        //Button action
        // Save New User button
        btnNewUser = (Button) rootView.findViewById(R.id.btAddUser);

        final EditText newUser = (EditText) rootView.findViewById(R.id.editTextAddUser);


        /**
         * Add new label button click listener
         * */
        btnNewUser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                boolean isConnectedToServerValue = false;
                //String user   = spinnerChooseUser.getSelectedItem().toString();
                String newUserValue = newUser.getText().toString();

                isConnectedToServer chkConn = new isConnectedToServer();
                try {
                    //isConnectedToServerValue = chkConn.execute(URL_SERVER).get();
                    isConnectedToServerValue = new isConnectedToServer().execute(URL_SERVER).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                if (isOnline()) {
                    if (isConnectedToServerValue) {
                        if (newUserValue.trim().length() > 0) {

//                        HashMap<String, String> valueUserTable = new HashMap<String, String>();
//
//                        // database handler
//                        //SQL_Definition database = new SQL_Definition(getActivity().getApplicationContext());

                            //valueUserTable.put("userName",newUserValue);
                            new PostAppUser().execute(newUserValue);

                            //database.insertUser(valueUserTable);

                            Toast.makeText(arg0.getContext(), "Inserting New User " + newUserValue,
                                    Toast.LENGTH_SHORT).show();

                            newUser.setText("");

                        } else {
                            Toast.makeText(arg0.getContext(), "Please enter New User name",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(arg0.getContext(), "Server is not available",
                                Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(arg0.getContext(), "Network is not available",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

/*Async Class to Add New User*/
public class  PostAppUser extends AsyncTask<String, Void, Void> {

    boolean isNewUserCreated = false;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ProgressDialog pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Creating new User..");
        pDialog.setCancelable(false);
        pDialog.show();
        Log.d("llamando a PostAppUser", "sincronizando");
    }
    @Override
    protected Void doInBackground(String... arg) {

        String newUserValue = arg[0];

        ConnectivityManager cm =
                (ConnectivityManager)getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();


        // // database handler
//            SQL_Definition database = new SQL_Definition(getApplicationContext());
//            String rowLimit = "10";
//            List<String> tracks = database.getTracksForSync(rowLimit);

        Log.d("newUserValue","newUserValue: " + newUserValue);

        if (isConnected) {
            if (newUserValue.trim().length() > 0) {

//                String dataTracks = "[";
//                for (int i = 0; i < tracks.size(); i++) {
//                    dataTracks = dataTracks + tracks.get(i) +",";
//                }
//                dataTracks = dataTracks.substring(0, dataTracks.length()-1) + "]";

                // Preparing post params
                List<NameValuePair> params = new ArrayList<NameValuePair>();
//                Log.d("trackdata",dataTracks);

                params.add(new BasicNameValuePair("dataUser", newUserValue));
                //            params.add(new BasicNameValuePair("name2", "NUEVO VALOR2"));

                ServiceHandler serviceClient = new ServiceHandler();

                Log.d("llamando a PostTrack WS", "WSWSWSWS");
                String json = serviceClient.makeServiceCall(URL_POST_USERS,ServiceHandler.POST, params);

                Log.d("Create User Response: ", "> " + json);

                if (json != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(json);
                        boolean error = jsonObj.getBoolean("error");

                        // checking for error node in json
                        if (!error) {
                            // new category created successfully
                            isNewUserCreated = true;

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

        } else {
            Toast.makeText(getContext(), "Please enter New User name",
                    //Toast.makeText(getApplicationContext(), "Please enter New User name",
                    Toast.LENGTH_SHORT).show();

        }


        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (pDialog.isShowing())
            pDialog.dismiss();
        if (isNewUserCreated) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // fetching all users
                    new GetUsers().execute();
                }
            });
        }
    }
}

/*Async Class to refresh Users*/
public class GetUsers extends AsyncTask<Void,Void,Void> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ProgressDialog pDialog = new ProgressDialog(getActivity().getApplicationContext());
        pDialog.setMessage("Fetching User information..");
        pDialog.setCancelable(false);
        pDialog.show();

    }

    @Override
    protected Void doInBackground(Void... params) {

        ServiceHandler jsonParser = new ServiceHandler();

        String json = jsonParser.makeServiceCall(URL_GET_USERS, ServiceHandler.GET);

        Log.d("GetUsers Response: ", "> " + json);

        if (json != null) {
            try {
                JSONObject mainJson = new JSONObject(json);
                // database handler
                SQL_Definition database = new SQL_Definition(getActivity().getApplicationContext());

                if (mainJson != null) {
                    JSONArray rows = mainJson.getJSONArray("rows");
                    Log.d("Users Rows", rows.toString());

                    for (int i = 0; i < rows.length(); i++) {

                        JSONObject jsonObject = rows.getJSONObject(i);

                        String userid = jsonObject.getString("appuserid");
                        String username = jsonObject.getString("appusername");

                        Log.d("userid: ", "> " + userid);
                        Log.d("username: ", "> " + username);


                        HashMap<String, String> valueUserTable = new HashMap<String, String>();



                        valueUserTable.put("userid",userid);
                        valueUserTable.put("username",username);

                        database.insertUser(valueUserTable);

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
            pDialog.setMessage("Fetching User information.. OK");
        pDialog.dismiss();
        //populateSpinner();


    }
}

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

//    public boolean isConnectedToServer(String url, int timeout) {
//        try{
//            URL myUrl = new URL(url);
//            HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
//            connection.setConnectTimeout(timeout);
//            connection.connect();
//            return true;
//        } catch (Exception e) {
//            // Handle your exceptions
//            Log.d("Server error", e.toString());
//            return false;
//        }
//    }
//
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
