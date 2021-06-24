package com.kanibalv.app;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

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
                    .add(R.id.container, new new_vehicleFragment())
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

   // /**
   //  * A placeholder fragment containing a simple view.
   //  */
   // public  class PlaceholderFragment extends Fragment {
   // }



}
