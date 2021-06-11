package com.kanibalv.app;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public final static String EXTRA_MESSAGE_USER           = "com.kanibalv.app.MESSAGE";
    public final static String EXTRA_MESSAGE_IdentiVehicle  = "com.kanibalv.MESSAGE";
    public final static String EXTRA_MESSAGE_ROUTE          = "com.kanibalv.MESSAGE";
    public static String IMEI;
    public boolean statusOfGPS=false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Keep screen ON
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragmentMainActivity())
                    .commit();
        }

        final TelephonyManager tm =(TelephonyManager)getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        //IMEI = tm.getDeviceId();
        IMEI = "1234";

        // database handler
        SQL_Definition database = new SQL_Definition(this.getApplicationContext());

        // Get license code from DB
        HashMap<String, String> licenseCode = database.getLicenseCode();

        if (licenseCode.isEmpty()){
            Log.d("Database is Empty", "Database License Code Empty");
            displayLicenseDialog();
        }
        else {
            Log.d("Database License CODE::::::",licenseCode.get(SQL_Definition.LICENSE_CODE));
            int licenseResult = checkLicenseCode(licenseCode.get(SQL_Definition.LICENSE_CODE));
            if (licenseResult == 0){
                Log.d("Database License CODE::::::", "license incorrect, displaying license dialog.");
                displayLicenseDialog();
            }
        }

        // Checking GPS State - init
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
        statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!statusOfGPS){
            Toast.makeText(this, "GPS is disable! please turn on", Toast.LENGTH_SHORT).show();
            createNotificationGPS(this);
        }
        else {
            Toast.makeText(this, "GPS is enable!", Toast.LENGTH_SHORT).show();
        };
        // Checking GPS State - end


        // Starting Syncronization Service -- init
        // For correct scheduling of the Service use the AlarmManager class.
        Calendar cal = Calendar.getInstance();

        Intent IntentNavigationService = new Intent(this, serverSync_service.class);
        PendingIntent pendingIntentNavigationService = PendingIntent.getService(this, 0, IntentNavigationService, 0);

        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        // Start every 60 seconds
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 90*1000, pendingIntentNavigationService);
        // Starting Syncronization Service -- end


        int activeSession;
        activeSession = database.checkSessionActive();
        database.close();

        if (activeSession == 0){
            Toast.makeText(this, "There is NO Active Session, Please Start a Session: " + activeSession,
                    Toast.LENGTH_SHORT).show();
            //Log.d("Session Value", "valor: "+activeSession);
        } else if (activeSession == 1) {
            Toast.makeText(this, "Resuming session: " + activeSession,
                    Toast.LENGTH_SHORT).show();
            //Log.d("Session Value", "valor: "+activeSession);
                //    You can check if the service was restarted via the Intent.getFlags() method.
                //    START_FLAG_REDELIVERY (in case the service was started with Service.START_REDELIVER_INTENT)
                //    or START_FLAG_RETRY (in case the service was started with Service.START_STICKY) is passed.

                //    // use this to start and trigger the servicio
                //    Intent Service= new Intent(context, Servicio.class);
                //// potentially add data to the intent
                //    Service.putExtra("KEY1", "Value to be used by the service");
                //    context.startService(Service);

            // Call Navigation View directly to resume it.
            Intent intentNavigationView = new Intent(this, navigation_view.class);
            //intentNavigationView.putExtra(EXTRA_MESSAGE_User, );
            startActivity(intentNavigationView);
//        } else if (activeSession == 2) {
//            Toast.makeText(this, "There is more than an Active Session, cancelling all session: " + activeSession,
//                    Toast.LENGTH_SHORT).show();
//            //Log.d("Session Value", "valor: "+activeSession);
//            // Calling function to reset Database
//            //database.getSessionActive();
//            //TODO: update all active session to 0
//        } else if (activeSession == 3) {
//            Toast.makeText(this, "There is a finnished session not sicronized: " + activeSession,
//                    Toast.LENGTH_SHORT).show();
//            //Log.d("Session Value", "valor: "+activeSession);
//            // Call Navigation View directly to resume it.
//            Intent intentNavigationView = new Intent(this, navigation_view.class);
//            //intentNavigationView.putExtra(EXTRA_MESSAGE_User, );
//            startActivity(intentNavigationView);
//        } else if (activeSession == 4) {
//            Toast.makeText(this, "There is a finnished session sicronized, but, tracks remains on device: " + activeSession,
//                    Toast.LENGTH_SHORT).show();
//            //Log.d("Session Value", "valor: "+activeSession);
//            // Call Navigation View directly to resume it.
//            Intent intentNavigationView = new Intent(this, navigation_view.class);
//            //intentNavigationView.putExtra(EXTRA_MESSAGE_User, );
//            startActivity(intentNavigationView);
        } else {
            Toast.makeText(this, "There is mixed information, Error.!! value: " + activeSession,
                    Toast.LENGTH_SHORT).show();
            //Log.d("Session Value", "valor: "+activeSession);
            //TODO: update all not finnished to ""
        }


    }

    private int checkLicenseCode(String licenseCode) {

        String deviceCode = IMEI + "salt";
        Log.d("Device CODE::::::",deviceCode);
        Log.d("License CODE:::::",licenseCode);
        if (licenseCode.equals(deviceCode)){
            Log.d("License Verification Result::::::","OK   -   Valid");
            return 1;
        }
        else {
            Log.d("License Verification Result::::::","NOK  -   Invalid");
            //return 0;
            return 1;
        }
    }

    private void displayLicenseDialog() {
        // Licensing is doing by reading a file line uncrypted with the to BF(IMEI+BFsalt)
        // Create popup dialog with input
        // Save value in DB.

        AlertDialog.Builder alertLicense = new androidx.appcompat.app.AlertDialog.Builder(this);

        alertLicense.setTitle("LICENSE");
        alertLicense.setMessage("Please enter License Code: " );

        // Set an EditText view to get user input
        final EditText inputLicense = new EditText(this);
        inputLicense.setSingleLine(true);
        alertLicense.setView(inputLicense);


        alertLicense.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Editable valueLicense = inputLicense.getText();

               int resultLicense =  checkLicenseCode(valueLicense.toString());
                // Calculate License
                // checkLicense = ;;;;;
                if (resultLicense == 0){
                    //Screen Message
                    Toast.makeText(getApplicationContext(), "Invalid License Code, please Restart and enter a Valid Code", Toast.LENGTH_SHORT).show();
                    exit();
                } else{
                    //saveLicenseValue(valueLicense.toString());
                    // database handler
                    SQL_Definition database = new SQL_Definition(getApplicationContext());
                    database.insertLicense(valueLicense.toString());
                    database.close();
                }
            }
        });

        alertLicense.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
                exit();
            }
        });

        alertLicense.show();

        return;
    }

    public void createNotificationGPS(MainActivity view) {
        // Prepare intent which is triggered if the
        // notification is selected --Settings.ACTION_LOCATION_SOURCE_SETTINGS
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Build notification
        // Actions are just fake
        Notification noti = new Notification.Builder(this)
                .setContentTitle("KEEPER GPS  ")
                .setContentText("Please turn ON GPS")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pIntent)
                .addAction(R.drawable.gps_off, "GPS ON", pIntent)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(1, noti);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }

        switch (item.getItemId()) {
            case R.id.action_settings:
                exit();
            case R.id.action_newUser:
                //Screen Message
                Toast.makeText(this, "Menu Item new User selected", Toast.LENGTH_SHORT).show();
                // Do this in response to button
                Intent intentNewUser = new Intent(this, new_user.class);
                //intentNewUser.putExtra(EXTRA_MESSAGE_PersonName, editTextPersonNameMessage);
                startActivity(intentNewUser);
                break;
            case R.id.action_newVehicle:
                //Screen Message
                Toast.makeText(this, "Menu item new Vehicle selected", Toast.LENGTH_SHORT).show();
                // Do this in response to button
                Intent intentNewVehicle = new Intent(this, new_vehicle.class);
                //intentNewUser.putExtra(EXTRA_MESSAGE_PersonName, editTextPersonNameMessage);
                startActivity(intentNewVehicle);
                break;
            case R.id.action_refreshUsers:
                Toast.makeText(this, "Menu item refresh Users selected", Toast.LENGTH_SHORT).show();
                // Do this in response to button
                Intent intentRefreshUsers = new Intent(this, refreshUsers.class);
                //intentNewUser.putExtra(EXTRA_MESSAGE_PersonName, editTextPersonNameMessage);
                startActivity(intentRefreshUsers);
                break;
            case R.id.action_refreshVehicles:
                Toast.makeText(this, "Menu item refresh Vehicles selected", Toast.LENGTH_SHORT).show();
                // Do this in response to button
                Intent intentRefreshVehicles = new Intent(this, refreshVehicles.class);
                //intentNewUser.putExtra(EXTRA_MESSAGE_PersonName, editTextPersonNameMessage);
                startActivity(intentRefreshVehicles);
                break;
            case R.id.action_refreshRoutes:
                Toast.makeText(this, "Menu item refresh Route selected", Toast.LENGTH_SHORT).show();
                // Do this in response to button
                Intent intentRefreshRoute = new Intent(this, refreshRoutes.class);
                //intentNewUser.putExtra(EXTRA_MESSAGE_PersonName, editTextPersonNameMessage);
                startActivity(intentRefreshRoute);
                break;

            default:
                break;
        }


        return super.onOptionsItemSelected(item);
    }







    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        // On selecting a spinner item
        String label = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "You selected: " + label,
                Toast.LENGTH_LONG).show();

    }
//
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }
    //function for break the app.s
    public void exit()
    {
        System.exit(0);
    }
}
