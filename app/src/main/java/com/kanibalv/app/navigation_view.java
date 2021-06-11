package com.kanibalv.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;


public class navigation_view extends AppCompatActivity {

    private String sessionID;
    private String sessionStatus;
    private String userId;
    private String vehicleId;
    private String routeId;
    private String sessionDate;

    private int activeSession = 0;
    private int sessionCanceled = 0;
    private String currentSessionStatus = "";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide Status BAR - init
        //        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ////        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        ////                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //
        //this.getActionBar().setDisplayShowHomeEnabled(false);
        this.getActionBar().setDisplayHomeAsUpEnabled(false);
        //Hide Status BAR - end

        setContentView(R.layout.activity_navigation_view);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragmentNavigationView());
            /*getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragmentNavigationView())
                    .commit();

             */
        }




        // use this to start and trigger Navigation Service
        Intent intentNavigationService= new Intent(this, navigation_service.class);
        //intentNavigationService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // potentially add data to the intent
        //i.putExtra("KEY1", "Value to be used by the service");
        if(startService(intentNavigationService) != null) {
            //Service is run
            Log.d("Ckecking Navigation Service status onCreate:", "Running");
        }else {
            //not running
            startService(intentNavigationService);
            Log.d("Ckecking Navigation Service status onCreate:", "Started");
        }



//        //For correct scheduling of the Service use the AlarmManager class.
//        Calendar cal = Calendar.getInstance();
//
//        Intent IntentNavigationService = new Intent(this, serverSync_service.class);
//        PendingIntent pendingIntentNavigationService = PendingIntent.getService(this, 0, IntentNavigationService, 0);
//
//        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
//        // Start every 30 seconds
//        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 30*1000, pendingIntentNavigationService);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_cancelSession:
                //Screen Message
                //Toast.makeText(this, "Menu Item Close Session", Toast.LENGTH_SHORT).show();

                cancelSession();
                // Do this in response to button
                Intent intentBackMainActivity = new Intent(this,
                        MainActivity.class);
                //intentNewUser.putExtra(EXTRA_MESSAGE_PersonName, editTextPersonNameMessage);
                startActivity(intentBackMainActivity);

                break;
//            case R.id.action_newVehicle:
//                //Screen Message
//                Toast.makeText(this, "Menu item new Vehicle selected", Toast.LENGTH_SHORT).show();
//                // Do this in response to button
//                Intent intentNewVehicle = new Intent(this, new_vehicle.class);
//                //intentNewUser.putExtra(EXTRA_MESSAGE_PersonName, editTextPersonNameMessage);
//                startActivity(intentNewVehicle);
//                break;
//            case R.id.action_refreshRoutes:
//                Toast.makeText(this, "Menu item refresh Route selected", Toast.LENGTH_SHORT).show();
//                // Do this in response to button
//                Intent intentRefreshRoute = new Intent(this, refreshRoutes.class);
//                //intentNewUser.putExtra(EXTRA_MESSAGE_PersonName, editTextPersonNameMessage);
//                startActivity(intentRefreshRoute);
//                break;

            default:
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    public void cancelSession() {

        // database handler
        SQL_Definition database = new SQL_Definition(this);

        //SQL_Definition database = new SQL_Definition(getApplicationContext());
        HashMap<String, String> session = new HashMap<String, String>();
        session = database.getSessionActive();

        sessionID     = session.get(SQL_Definition.SESSION_ID);
        sessionStatus = session.get(SQL_Definition.SESSION_STATUS);
        userId        = session.get(SQL_Definition.USER_ID);
        vehicleId     = session.get(SQL_Definition.VEHICLE_ID);
        routeId       = session.get(SQL_Definition.ROUTE_ID);
        sessionDate   = session.get(SQL_Definition.CREATED_AT);


        session.remove(SQL_Definition.SESSION_STATUS);
        session.put(SQL_Definition.SESSION_STATUS,"2");

        activeSession = database.checkSessionActive();

        TextView displaySync = (TextView)findViewById(R.id.textValueSync);
        currentSessionStatus = (String) displaySync.getText();

        if(activeSession == 1 ){

            Toast.makeText(this, "No running Session, NOT Canceled", Toast.LENGTH_SHORT).show();
            return;
        }

            // Update return rows affected.
            sessionCanceled = database.updateSession(session);
            database.close();


            if (sessionCanceled > 0){
                Toast.makeText(this, "Session Canceled", Toast.LENGTH_SHORT).show();
                return;
            } else {
                Toast.makeText(this, "Session NOT Canceled", Toast.LENGTH_SHORT).show();
                return;
            }
    }

    @Override
    public void onBackPressed()
    {

        // super.onBackPressed(); // Comment this super call to avoid calling finish()
    }


    //RECEIVER from Service to update data Values on the View
    public BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();

            if (bundle != null) {
                String recVelocity = intent.getStringExtra(navigation_service.SPEED);
                String recLongitude = intent.getStringExtra(navigation_service.LONGITUDE);
                String recLatitude = intent.getStringExtra(navigation_service.LATITUDE);
                String recStatus = intent.getStringExtra(navigation_service.STATUS);
                String recSync = intent.getStringExtra(navigation_service.SYNCCOUNT);
                String recCheckPoint = intent.getStringExtra(navigation_service.CHECKPOINT);

                TextView displaySpeed = (TextView)findViewById(R.id.textValueVelocity);
                displaySpeed.setTextSize(40);
                displaySpeed.setTextColor(Color.DKGRAY);
                displaySpeed.setText(recVelocity);

                TextView displayLongitude = (TextView)findViewById(R.id.textValueLongitude);
                displayLongitude.setText(recLongitude);

                TextView displayLatitude = (TextView)findViewById(R.id.textValueLatitude);
                displayLatitude.setText(recLatitude);

                TextView displayStatus = (TextView)findViewById(R.id.textValueStatus);
                displayStatus.setText(recStatus);

                TextView displaySync = (TextView)findViewById(R.id.textValueSync);
                displaySync.setText(recSync);

                if (recSync.equals("Synchronized")){
                    displaySync.setBackgroundColor(getResources().getColor(R.color.green));
                } else {
                    displaySync.setBackgroundColor(getResources().getColor(R.color.grey_default));
                };

                TextView displayCheckPoint = (TextView)findViewById(R.id.textValueCheckPoint);
                displayCheckPoint.setText(recCheckPoint);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(navigation_service.NOTIFICATION));

        // use this to start and trigger Navigation Service
        Intent intentNavigationService= new Intent(this, navigation_service.class);
        //intentNavigationService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // potentially add data to the intent
        //i.putExtra("KEY1", "Value to be used by the service");


        if(startService(intentNavigationService) != null) {
            //Service is run
            Log.d("Ckecking Navigation Service status onResume:", "Running");
        }else {
            //not running
            startService(intentNavigationService);
            Log.d("Ckecking Navigation Service status onResume:", "Started");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregisterReceiver(receiver);
    }

}
