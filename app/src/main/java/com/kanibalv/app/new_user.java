package com.kanibalv.app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


public class new_user extends AppCompatActivity {


    // API urls
    // Url to WS
    //private String URL_SERVER = "http://kanibal.servebeer.com:3000/";
    //private String URL_SERVER = "http://172.31.110.195:3000/";
    //private String URL_GET_USERS  = URL_SERVER + "getUsers";
    //private String URL_POST_USERS = URL_SERVER + "postAppUsers";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new new_userFragment())
                    .commit();
        }

    }


    // Action Bar Disabled (Commented)
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.new_user, menu);
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
