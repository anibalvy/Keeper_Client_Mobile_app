package com.kanibalv.app;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class refreshUsers extends AppCompatActivity {

    ProgressDialog pDialog;

    // API urls
    // Url to get all Routes

   // private final String URL_SERVER = "http://kanibal.servebeer.com:3000/";
    //private String URL_SERVER = "http://172.31.110.195:3000/";
    //private final String URL_GET_USERS  = URL_SERVER + "getUsers";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh_users);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new refreshUsersFragment())
                    .commit();
        }
    }

// Action Bar Disabled (Commented) -- AVY
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.refresh_users, menu);
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

}
