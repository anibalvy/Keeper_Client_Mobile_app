package com.kanibalv.app;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

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
                    .add(R.id.container, new refreshRoutesFragment())
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
//    public  class PlaceholderFragment extends Fragment {


}
