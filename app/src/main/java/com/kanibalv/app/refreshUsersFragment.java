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

import static com.kanibalv.app.ServiceHandler.URL_GET_USERS;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link refreshUsersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
/**
 * A placeholder fragment containing a simple view.
 */
public class refreshUsersFragment extends Fragment {

    Button btnRefreshUsers;

    public refreshUsersFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_refresh_users, container, false);

        btnRefreshUsers = (Button) rootView.findViewById(R.id.btnRefreshUsers);

        btnRefreshUsers.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                new GetUsers().execute();
            }
        });

        return rootView;
    }



/*Async Class to refresh Users*/
public class GetUsers extends AsyncTask<Void,Void,Void> {

    @Override
    protected void onPreExecute() {
        //protected void onPreExecute() {
        super.onPreExecute();
        ProgressDialog pDialog = new ProgressDialog(getActivity().getApplicationContext());
        //ProgressDialog pDialog = new ProgressDialog(getActivity(), getActivity().getTheme());
        pDialog.setMessage("Fetching User information..");
        pDialog.setCancelable(false);
        pDialog.setIndeterminate(true);
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
        ProgressDialog pDialog = new ProgressDialog(getActivity().getApplicationContext());
        if (pDialog.isShowing())
            pDialog.setMessage("Fetching User information.. OK");
        pDialog.dismiss();
        //populateSpinner();


    }
}
}