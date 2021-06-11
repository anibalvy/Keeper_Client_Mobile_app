package com.kanibalv.app;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class navigation_service extends Service implements TextToSpeech.OnInitListener {

    public static final String SPEED = "SPEED";
    public static final String LONGITUDE = "LONGITUDE";
    public static final String LATITUDE = "LATITUDE";
    public static final String NOTIFICATION = "com.kanibalv.app.currentValues";
    public static final String STATUS = "STATUS";
    public static final String SYNCCOUNT = "SYNCCOUNT";
    public static final String CHECKPOINT = "CHECKPOINT";

    // initializing variables
    public double currentLongitude;
    public double currentLatitude;
    public double currentAltitude = 0;
    public double currentSpeed;
    public double lastSpeed = 10000;


    //Constants for Location Manager SetUp
    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 20; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 200; // in Milliseconds


    // Session information
    private int activeSession = 0;
    private int trackType;


    private String sessionID;
    private String sessionStatus;
    private String userId;
    private String vehicleId;
    private String routeId;
    private String sessionDate;


    private String userName;
    private String vehicleIdentification;
    private String routeData;
    private Boolean gpsEnable = false;

    // Global Variables
    // Tolerance to calculate distance between points
    private double tolerance = 0.001;

    double velocityGlobal = 90;
    double velocityMax = 0;
    double velocityThreshold = velocityGlobal;

    // show
    String checkpointdesc = "";
    String status = "Running";
    String tracksCount = "";

    private TextToSpeech talker;

    public navigation_service() {
        super();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //this service will run until is manually stopped
        Toast.makeText(this, "Navigation Service started!!", Toast.LENGTH_LONG).show();

        talker = new TextToSpeech(this, (TextToSpeech.OnInitListener) this);
        //talker.speak("Starting Navigation", TextToSpeech.QUEUE_FLUSH, null);
        talker.speak("Servicio Navegación iniciado", TextToSpeech.QUEUE_FLUSH, null);

        //  timer definition to 10 segundos para evitar repeticiones
        TimerTask scanTask;
        final Handler handler = new Handler();
        Timer t = new Timer();


        //Check if exist an open Session
        //TODO
        //Check if exist an open Session and redirects to Navigation Activity.s
        // database handler
        SQL_Definition database = new SQL_Definition(this.getApplicationContext());
        activeSession = database.checkSessionActive();
        database.close();

        // CHECK RUNNING SESSIONS, it must exist only one.
        if (activeSession == 0) {
            Toast.makeText(this, "There is NO Active Session, Please Start a Session: " + activeSession,
                    Toast.LENGTH_SHORT).show();

            // Call MainActivity to Start one.
            Intent intentMainActivity = new Intent(this, MainActivity.class);
            intentMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //intentNewUser.putExtra(EXTRA_MESSAGE_PersonName, editTextPersonNameMessage);
            startActivity(intentMainActivity);
            //Log.d("Session Value", "valor: "+activeSession);
//        } else if (activeSession == 1 || activeSession == 3 || activeSession == 4) {
        } else if (activeSession == 1) {
            Toast.makeText(this, "There is a Active Session, Starting Navigation service. " + activeSession,
                    Toast.LENGTH_SHORT).show();
            Log.d("NavigationService", "Active Session == 1    ");


            // Open Session Data.
            // database handler
            //SQL_Definition database = new SQL_Definition(getApplicationContext());
            HashMap<String, String> session = new HashMap<String, String>();
            session = database.getSessionActive();

            sessionID = session.get(SQL_Definition.SESSION_ID);
            sessionStatus = session.get(SQL_Definition.SESSION_STATUS);
            userId = session.get(SQL_Definition.USER_ID);
            vehicleId = session.get(SQL_Definition.VEHICLE_ID);
            routeId = session.get(SQL_Definition.ROUTE_ID);
            sessionDate = session.get(SQL_Definition.CREATED_AT);

            HashMap<String, String> user = new HashMap<String, String>();
            user = database.getUserById(userId);
            userName = user.get(SQL_Definition.USER_NAME);


            HashMap<String, String> vehicle = new HashMap<String, String>();
            vehicle = database.getVehicleById(vehicleId);
            vehicleIdentification = vehicle.get(SQL_Definition.VEHICLE_IDENTIFIER);

            HashMap<String, String> route = new HashMap<String, String>();
            route = database.getRouteById(routeId);
            routeData = route.get(SQL_Definition.ROUTE_DATA);


            //Updating Session Status to Running
//            sessionStatus = "1";
//            session.remove(SQL_Definition.SESSION_STATUS);
//            session.put(SQL_Definition.SESSION_STATUS,sessionStatus);
//            database.updateSession(session);

            database.close();
            Log.d("Ruta", routeData);
            //Toast.makeText(this,"Navigation Service started for:\n" + userName
            //       + "\nin Vehicle: " + vehicleIdentification
            //       + "\nand route Data: \n" + routeData,Toast.LENGTH_LONG).show();

            //Parse Route
            // Schedule to Save SPEED
            //reseteando avisos
            scanTask = new TimerTask() {
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {

                            Log.d("Valor GPS", gpsEnable.toString());

                            if (gpsEnable.equals(Boolean.TRUE) && (currentSpeed != lastSpeed)) {
                                //TODO:escribir_vel();
                                Log.d("SPEEDTimer", "escribiendo velocidad según timer, current: " + currentSpeed + ", last: " + lastSpeed);
                                trackType = 1; //1 = Velocity Check.
                                writeTrack(trackType, "Vel. Actual: " + currentSpeed);
                                lastSpeed = currentSpeed;


                                // Checking VMAX - INIT
//                                // Search for the string, gives "-1" at not found.
//                                indexValue = jsonObject.getString("name").indexOf("VELMAX=");
//
//                                Log.d("Valor del Index",Double.valueOf(indexValue).toString());
//                                if (indexValue != -1){
//
//                                    // Get Value of new Max Velocity
//                                    velocityMax=Double.valueOf(jsonObject.getString("name").substring(indexValue+7, indexValue+9));
//                                    Log.d("Velocity MAX",Double.valueOf(velocityMax).toString());
//                                    Log.d("Velocity Global",Double.valueOf(velocityGlobal).toString());
//
//                                    // Check against Global Max Velocity (it must not be bigger)
//                                    if (velocityMax < velocityGlobal){
//                                        velocityThreshold = velocityMax;
//                                        Log.d("Velocity Threshold",Double.valueOf(velocityThreshold).toString());
//                                    }
//                                }
//
//                                // Check Speed again for accuracy.
//                                currentSpeed = roundValue(location.getSpeed()*3.6,0);
                                if (currentSpeed > velocityThreshold) {
                                    talker.speak("Se ha sobrepasado la velocidad Máxima Tramo ", TextToSpeech.QUEUE_ADD, null);
                                    Toast.makeText(getBaseContext(), "Se ha sobrepasado la velocidad Máxima Tramo ",
                                            Toast.LENGTH_SHORT).show();

                                    //TODO:evento(getDateTime()+"     "+"SE HA SUPERADO LA VELOCIDAD MAXIMA DEL TRAMO EN: "+Redondear(longitud,5)+"    "+Redondear(latitud,5)+"  VEL: "+String.valueOf( Redondear((location.getSpeed()*3.6),0))+"  VELMAX: "+String.valueOf(velmax_global)+ "  IDA:"+sentido_ruta);
                                    String param = "Sobre Vmax de " + velocityThreshold + " Vactual = " + currentSpeed;

                                    //Saving Event
                                    trackType = 3; //3 = MAX VEL..
                                    writeTrack(trackType, param.toString());

                                    // ERROR , a borrarse
                                    // velocityThreshold = velocityGlobal;

                                }
                                // Checking VMAX - END

                            } else if (gpsEnable.equals(Boolean.FALSE)) {
                                Log.d("SPEEDTimer", "GPS Desactivado");
                            } else {
                                Log.d("SPEEDTimer", "Same as last Speed, not saving it.");
                            }
                        }
                    });
                }
            };
            // Timer execution every 55 seg after 500ms of every execution.
            t.schedule(scanTask, 500, 55000);


            // GPS Coordinates provider
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                //return TODO;
                Log.d("INFO","TODO");
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                //return TODO;
                Log.d("INFO","TODO");
            }
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MINIMUM_TIME_BETWEEN_UPDATES,
                    MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
                    new MyLocationListener()
            );


//        } else if (activeSession == 2) {
//            Toast.makeText(this, "There is more than an Active Session, cancelling all session: " + activeSession,
//                    Toast.LENGTH_SHORT).show();
//            //Log.d("Session Value", "valor: "+activeSession);
//            // Calling function to reset Database
//            //database.getSessionActive();
        } else {
            Toast.makeText(this, "There is mixed information, Error.!! value: " + activeSession,
                    Toast.LENGTH_SHORT).show();
            //Log.d("Session Value", "valor: "+activeSession);
        }

        return START_STICKY;
    }

    private void writeTrack(int trackType, String eventData) {

        String trackData = "NI";

        trackData = "{\"longitude\":\"" + currentLongitude + "\",\"latitude\":\"" + currentLatitude + "\",\"altitude\":\""+currentAltitude+"\",\"speed\":\"" + currentSpeed + "\",\"event\":\"" + eventData + "\"}";

        // database handler
        SQL_Definition database = new SQL_Definition(getApplicationContext());

        //Save Track.
        HashMap<String, String> valueTrackTable = new HashMap<String, String>();

        valueTrackTable.put(SQL_Definition.SESSION_ID,sessionID);
        valueTrackTable.put(SQL_Definition.TRACKS_TYPE,String.valueOf(trackType));
        valueTrackTable.put(SQL_Definition.TRACKS_DATA,trackData);

        database.insertTracks(valueTrackTable);
        database.close();
        return;
    }

    public class MyLocationListener implements LocationListener {

        int indexValue = 0;
        int endValue = 0;

        // Variables for Navigation View Values
//        private String status    = "Running";
//        private String tracksCount = "";
        private String sessionId = "";
        private String sessionNavigation = "";
        private String sessionStatus = "";
        //String checkpointdesc = "";

        @Override
        public void onLocationChanged(Location location) {

            currentLongitude=location.getLongitude();
            currentLatitude=location.getLatitude();
            currentSpeed=roundValue(location.getSpeed()*3.6,0);
            gpsEnable = true;

            Log.d("SPEED",String.valueOf(roundValue(currentSpeed,0)));
            Log.d("Longitude",String.valueOf(roundValue(currentLongitude,6)));
            Log.d("Latitude",String.valueOf(roundValue(currentLatitude,6)));

            // Open Session Data.
            // database handler
            SQL_Definition database = new SQL_Definition(getApplicationContext());
            HashMap<String, String> session = new HashMap<String, String>();
            session = database.getSessionActive();

            sessionId     = session.get(SQL_Definition.SESSION_ID);
            sessionNavigation = session.get(SQL_Definition.ROUTE_NAVIGATION);
            sessionStatus = session.get(SQL_Definition.SESSION_STATUS);

            try {
                Log.d("sessionNavigation:" , sessionNavigation);
                JSONObject mainJson = new JSONObject(sessionNavigation);

                Log.d("mainJson FROM GPS",mainJson.toString());

                //JSONArray jsonArray = mainJson.getJSONObject("Folder").getJSONArray("Placemark");
                JSONArray jsonArray = mainJson.getJSONArray("data");
                Log.d("JSONarray FROM GPS",jsonArray.toString());


                Log.i(navigation_service.class.getName(),"Number of entries " + jsonArray.length());

                for (int i = 0; i < jsonArray.length(); i++) {
                    //JSONObject jsonObject = jsonArray.getJSONObject(i);
                    JSONObject jsonObject = mainJson.getJSONArray("data").getJSONObject(i);


                    Log.d("JSON OBJECT BEFORE MOD","JSON OBJECT BEFORE MOD  #########################");
                    Log.d(navigation_service.class.getName(), jsonObject.toString());
                    Log.d("name",jsonObject.getString("name"));

                    if (jsonObject.has("description")){
                        Log.d("description",jsonObject.getString("description"));
                    }
                        Log.d("Point",jsonObject.getString("Point"));
                        Log.d("Coordinates",jsonObject.getJSONObject("Point").getString("coordinates"));

                        List<String> list = new ArrayList<String>(Arrays.asList(jsonObject.getJSONObject("Point").getString("coordinates").split(",")));

                    Log.d("longitude", list.get(0));
                        Log.d("latitude",list.get(1));
                        Log.d("altitude",list.get(2));

                    //TODO: logic
                    // Check Point by Point if is close to current Location and it was not readed
                    if (checkPoint(Double.valueOf(list.get(0)).doubleValue(),Double.valueOf(list.get(1)),currentLongitude,currentLatitude,tolerance)
                            && !(jsonObject.has("flagRead"))){

                        if (jsonObject.has("description")){
                            Toast.makeText(getBaseContext(), jsonObject.getString("description"),
                                    Toast.LENGTH_SHORT).show();
                            talker.speak(jsonObject.getString("description"), TextToSpeech.QUEUE_FLUSH, null);
                            //Saving Event
                            trackType = 4; //3 = Route point..
                            writeTrack(trackType, jsonObject.getString("description"));
                            Log.d("description",jsonObject.getString("description"));
                            checkpointdesc =  jsonObject.getString("description");
                            Log.d("checkpointdesc",checkpointdesc);

                        }

                        // Set flag already read.
                        mainJson.getJSONArray("data").getJSONObject(i).put("flagRead",true);
                        Log.d("JSON OBJECT AFTER MOD","JSON OBJECT AFTER MOD  #########################");
                        Log.d(navigation_service.class.getName(), jsonObject.toString());

                        // Checking VMAX - INIT
                        // Search for the string, gives "-1" at not found.
                        indexValue = jsonObject.getString("name").indexOf("VELMAX=");

                        Log.d("Valor del Index",Double.valueOf(indexValue).toString());
                        if (indexValue != -1){

                            // Get Value of new Max Velocity
                            velocityMax=Double.valueOf(jsonObject.getString("name").substring(indexValue+7, indexValue+9));
                            Log.d("Velocity MAX",Double.valueOf(velocityMax).toString());
                            Log.d("Velocity Global",Double.valueOf(velocityGlobal).toString());

                            // Check against Global Max Velocity (it must not be bigger)
                            if (velocityMax < velocityGlobal){
                                velocityThreshold = velocityMax;
                                Log.d("Velocity Threshold",Double.valueOf(velocityThreshold).toString());
                            }
                        }

                        // Check Speed again for accuracy.
                        currentSpeed = roundValue(location.getSpeed()*3.6,0);
                        if( currentSpeed > velocityThreshold )
                        {
                            talker.speak("Se ha sobrepasado la velocidad Máxima Tramo ", TextToSpeech.QUEUE_ADD, null);
                            Toast.makeText(getBaseContext(), "Se ha sobrepasado la velocidad Máxima Tramo ",
                                    Toast.LENGTH_SHORT).show();

                            //TODO:evento(getDateTime()+"     "+"SE HA SUPERADO LA VELOCIDAD MAXIMA DEL TRAMO EN: "+Redondear(longitud,5)+"    "+Redondear(latitud,5)+"  VEL: "+String.valueOf( Redondear((location.getSpeed()*3.6),0))+"  VELMAX: "+String.valueOf(velmax_global)+ "  IDA:"+sentido_ruta);
                            String param = "Sobre Vmax de "+ velocityThreshold + " Vactual = " + currentSpeed;

                            //Saving Event
                            trackType = 3; //3 = MAX VEL..
                            writeTrack(trackType,param.toString());

                            // ERROR , a borrarse
                            // velocityThreshold = velocityGlobal;

                        }
                        // Checking VMAX - END

                        // Checking ENDROUTE - INIT
                        endValue = jsonObject.getString("name").indexOf("ENDROUTE");
                        if (endValue != -1) {
                            // Setting Status to Finished (= 3)
                            endSession();
                            status = "Route Ended, wait for the track to be upload to Server before close";
                        }
                        // Checking ENDROUTE - END
                    }
                }

                //TODO: Salvar mainJson
                Log.d("checkpointdesc2",checkpointdesc);
                session.remove(SQL_Definition.ROUTE_NAVIGATION);
                session.put(SQL_Definition.ROUTE_NAVIGATION,mainJson.toString());
                database.updateSessionRoute(session);

            } catch (Exception e) {
                e.printStackTrace();
            }

            tracksCount = database.getTracksCount();
            database.close();
            Log.d("checkpointdesc2",checkpointdesc);

            //Display Current Values
            showCurrentValues(String.valueOf(roundValue(currentSpeed, 0)),
                    String.valueOf(roundValue(currentLongitude, 6)),
                    String.valueOf(roundValue(currentLatitude, 6)),
                    status,
                    tracksCount,
                    checkpointdesc);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(),
                    "Se ha encendido el sistema GPS",
                    Toast.LENGTH_LONG).show();
            //TODO:evento(getDateTime()+"     "+"SE HA Encendido EL GPS "+Redondear(currentLongitude,5)+"    "+Redondear(currentLatitude,5));
            trackType = 2; // 2: GPS Event
            writeTrack(trackType, "GPS ON");
            gpsEnable=true;
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(),
                    "SE HA APAGADO EL GPS-A ",
                    Toast.LENGTH_LONG).show();

            //TODO:evento(getDateTime()+"     "+"SE HA APAGADO EL GPS "+Redondear(currentLongitude,5)+"    "+Redondear(currentLatitude,5));
            trackType = 2; // 2: GPS Event
            writeTrack(trackType, "GPS OFF");
            gpsEnable=false;
            // Display GPS Notification
            createNotificationGPS();
        }
    }




    @Override
    public void onDestroy(){
        super.onDestroy();

        Toast.makeText(this,"Navigation Service Stopped.",Toast.LENGTH_LONG).show();
    }

    //BroadcastReceiver Message to Navigation View
    private void showCurrentValues(String speed, String longitude, String latitude, String status, String SyncCount, String checkpointdesc) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(SPEED, speed);
        intent.putExtra(LONGITUDE, longitude);
        intent.putExtra(LATITUDE, latitude);
        intent.putExtra(STATUS, status);
        intent.putExtra(SYNCCOUNT,SyncCount);
        intent.putExtra(CHECKPOINT,checkpointdesc);
        sendBroadcast(intent);
    }

    public double roundValue(double number,int digits)
    {
        int value=(int) Math.pow(10,digits);
        return Math.rint(number*value)/value;
    }

    //funcion para calcular distancia en metros entre dos puntos geograficos usando trigonometria esferica
    int Distance(double lon1, double lat1,
                 double lon2, double lat2) {


        double earthRadius = 6371; // km

        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        double dlon = (lon2 - lon1);
        double dlat = (lat2 - lat1);

        double sinlat = Math.sin(dlat / 2);
        double sinlon = Math.sin(dlon / 2);

        double a = (sinlat * sinlat) + Math.cos(lat1)*Math.cos(lat2)*(sinlon*sinlon);
        double c = 2 * Math.asin (Math.min(1.0, Math.sqrt(a)));

        double distanceInMeters = earthRadius * c * 1000;

        return (int)distanceInMeters;



    }

    boolean checkPoint(double lon1, double lat1,double lon2, double lat2,double tol)
    {
        if ((Math.abs(lat2-lat1)< tol) && (Math.abs(lon2-lon1)< tol))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    //Talker onInit method
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {


            Locale locSpanish = new Locale("spa", "CL");
            int result = talker.setLanguage(locSpanish);
            //talker.setPitch((float) 0.1);
            //talker.setSpeechRate(2);


            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                String text = "Servicio Navegacion iniciado";
                //talker.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
            Log.d("TTS", "Initilization OK! on Nav Service");

        } else {
            Log.e("TTS", "Initilization Failed!on Nav Service");
        }

    }

    // Function to set session Status to "Finnish = 3"
    public void endSession() {

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
        session.put(SQL_Definition.SESSION_STATUS,"3");
        int sessionCompleted = 0;
        // Update return rows affected.
        sessionCompleted = database.updateSession(session);

        if (sessionCompleted > 0){
            Toast.makeText(this, "Session Finished, Wait to Tracks be Upload to Server.", Toast.LENGTH_SHORT).show();
            createNotificationSessionComplete();
            return;
        } else {
            Toast.makeText(this, "Session NOT Finished", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void createNotificationSessionComplete() {
        // Build notification
        Notification noti = new Notification.Builder(this)
                .setContentTitle("Keeper GPS Session Complete")
                .setContentText("Espere que las datos guardados se sincronicen.")
                .setSmallIcon(R.drawable.ic_launcher).build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(getBaseContext().NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(2, noti);

    }

    public void createNotificationGPS() {
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


}
