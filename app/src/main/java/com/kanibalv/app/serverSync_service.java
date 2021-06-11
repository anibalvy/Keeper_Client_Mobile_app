package com.kanibalv.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.kanibalv.app.ServiceHandler.URL_POST_SESSIONS;
import static com.kanibalv.app.ServiceHandler.URL_POST_TRACKS;


public class serverSync_service extends Service {

    // API urls
    // Url to WS
    //private String URL_SERVER = "http://kanibal.servebeer.com:3000/";
    //private String URL_SERVER = "http://192.168.1.167:4000/";
    //private String URL_POST_TRACKS   = URL_SERVER +"postTracks";
    //private String URL_POST_SESSIONS = URL_SERVER + "postSessions";

    private boolean isConnectedToServerValue = false;

    private String tracksCount = "";
    private String tracksCountMessage = "";

    public serverSync_service() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        //this service will run until is manually stopped
        Toast.makeText(this, "synchronizing", Toast.LENGTH_LONG).show();



        isConnectedToServer chkConn = new isConnectedToServer();
        try {
            //isConnectedToServerValue = chkConn.execute(URL_SERVER).get();
            isConnectedToServerValue = chkConn.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // Notification of Sync Status
        createNotification();

        if ((isOnline()) && (isConnectedToServerValue)) {
            new PostSessions().execute();

            new PostTracks().execute();

        }

        return START_STICKY;
    }



    @Override
    public void onDestroy(){
        super.onDestroy();
        Toast.makeText(this,"Server Synchronization Service Stopped.",Toast.LENGTH_LONG).show();
    }

    /*Async Class to refresh Routes*/
    public class PostSessions extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//                pDialog = new ProgressDialog(MainActivity.this);
//                pDialog.setMessage("Creating new category..");
//                pDialog.setCancelable(false);
//                pDialog.show();
            Log.d("llamado a PostSessions", "sincronizando");
        }
        @Override
        protected Void doInBackground(String... arg) {

            // // database handler
            SQL_Definition database = new SQL_Definition(getApplicationContext());

            List<String> sessions = database.getAllSessionsJson();
            database.close();

            Log.d("session SIZE","objetos son: " + sessions.size());

            if (sessions.size() > 0) {

                String dataSessions = "[";
                for (int i = 0; i < sessions.size(); i++) {
                    dataSessions = dataSessions + sessions.get(i) +",";
                }
                dataSessions = dataSessions.substring(0, dataSessions.length()-1) + "]";

                // Preparing post params
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                Log.d("session", dataSessions);

                params.add(new BasicNameValuePair("dataSessions", dataSessions));
                //            params.add(new BasicNameValuePair("name2", "NUEVO VALOR2"));

                ServiceHandler serviceClient = new ServiceHandler();

                Log.d("llamado a PostSessions WS", "WS dataSessions");

                String json = serviceClient.makeServiceCall(URL_POST_SESSIONS,ServiceHandler.POST, params);

                Log.d("Create Response: ", "> " + json);

                if (json != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(json);
//                        boolean error = jsonObj.getBoolean("error");
//
//                        // checking for error node in json
//                        if (!error) {
//                            // new category created successfully
//                            //isNewCategoryCreated = true;
//
//                        } else {
//                            //Log.e("Create Category Error: ", "> " + jsonObj.getString("message"));
//                            Log.e("Create Category Error: ", "> " );
//                        }
                        JSONArray jsonArray = jsonObj.getJSONArray("fn_upsertsession");
                        Log.d("JSONarray FROM inserttracks",jsonArray.toString());
                        for (int i = 0; i < jsonArray.length(); i++){
                            JSONObject jsonObject = jsonObj.getJSONArray("fn_upsertsession").getJSONObject(i);

//                            String trackIdToDelete = jsonObject.getString("sessionid");
                            String sessionIdToUpdate = jsonObject.getString("sessionid");
                            // // database handler
                            //SQL_Definition database = new SQL_Definition(getApplicationContext());
//                            database.deleteTrack(trackIdToDelete);

                            database.updateSessionIdFinnishedAndEmailed(sessionIdToUpdate);
                            database.close();
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
//            if (pDialog.isShowing())
//                pDialog.dismiss();
//            if (isNewCategoryCreated) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                    // fetching all categories
//
//                        new GetCategories().execute();
//                    }
//                });
//            }
        }
    }


    /*Async Class to refresh Routes*/
   public class PostTracks extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//                pDialog = new ProgressDialog(MainActivity.this);
//                pDialog.setMessage("Creating new category..");
//                pDialog.setCancelable(false);
//                pDialog.show();
            Log.d("llamando a PostTrack", "sincronizando");
        }
        @Override
        protected Void doInBackground(String... arg) {

            //String newCategory = arg[0];



            // // database handler
            SQL_Definition database = new SQL_Definition(getApplicationContext());

            //Limit of
            String rowLimit = "20";
            List<String> tracks = database.getTracksForSync(rowLimit);
            database.close();

            Log.d("tracks SIZE","objetos son: " + tracks.size());

            if (tracks.size() > 0) {

                String dataTracks = "[";
                for (int i = 0; i < tracks.size(); i++) {
                    dataTracks = dataTracks + tracks.get(i) +",";
                }
                dataTracks = dataTracks.substring(0, dataTracks.length()-1) + "]";

                // Preparing post params
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                Log.d("trackdata",dataTracks);

                params.add(new BasicNameValuePair("dataTracks", dataTracks));
    //            params.add(new BasicNameValuePair("name2", "NUEVO VALOR2"));

                ServiceHandler serviceClient = new ServiceHandler();


                Log.d("llamando a PostTrack WS", "WSWSWSWS");
                String json = serviceClient.makeServiceCall(URL_POST_TRACKS,ServiceHandler.POST, params);

                Log.d("Create Response: ", "> " + json);

                if (json != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(json);
//                        boolean error = jsonObj.getBoolean("error");

//
//                        // checking for error node in json
//                        if (!error) {
//                            // new category created successfully
//                            //isNewCategoryCreated = true;
//
//                        } else {
//                            //Log.e("Create Category Error: ", "> " + jsonObj.getString("message"));
//                            Log.e("Create Category Error: ", "> " );
//                        }
                        JSONArray jsonArray = jsonObj.getJSONArray("fn_inserttracks");
                        Log.d("JSONarray FROM inserttracks",jsonArray.toString());
                        for (int i = 0; i < jsonArray.length(); i++){
                            JSONObject jsonObject = jsonObj.getJSONArray("fn_inserttracks").getJSONObject(i);

                            String trackIdToDelete = jsonObject.getString("trackid");
                            // // database handler
                            //SQL_Definition database = new SQL_Definition(getApplicationContext());
                            database.deleteTrack(trackIdToDelete);
                            database.close();
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
//            if (pDialog.isShowing())
//                pDialog.dismiss();
//            if (isNewCategoryCreated) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                    // fetching all categories
//
//                        new GetCategories().execute();
//                    }
//                });
//            }
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

    public void createNotification() {

        SQL_Definition database = new SQL_Definition(this.getApplicationContext());
        tracksCount = database.getTracksCount();
        database.close();

        if (tracksCount.equals("Synchronized")){
            tracksCountMessage = "Tracks sincronizados";
        } else {
            tracksCountMessage = "Quedan " + tracksCount + " por sincronizar, asegure conexión red.";
        };
        // Build notification
        Notification noti = new Notification.Builder(this)
                .setContentTitle("Keeper GPS Status")
                .setContentText(tracksCountMessage )
                .setSmallIcon(R.drawable.ic_launcher).build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(getBaseContext().NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, noti);


    }

}
