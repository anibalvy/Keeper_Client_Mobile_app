package com.kanibalv.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by kanibal on 1/15/14.
 * Description:
 * Class to define Database and Table Databases.
 */
//Helper class for SQLite
public class SQL_Definition extends SQLiteOpenHelper {


    private static final String DATABASE_NAME   =   "keeper.db";
    private static final int DATABASE_VERSION   =   1;

    //created_at DATETIME DEFAULT CURRENT_TIMESTAMP
    public static final String CREATED_AT = "created_at";

    //Parameters Table Users
    public static final String TABLE_LICENSE  = "license";
    public static final String LICENSE_ID      = "licenseId";
    public static final String LICENSE_CODE    = "licenseCode";

    //Parameters Table Users
    public static final String TABLE_USERS  = "users";
    public static final String USER_ID      = "userId";
    public static final String USER_NAME    = "userName";
    //created_at DATETIME DEFAULT CURRENT_TIMESTAMP

    //Parameter Table Vehicles
    public static final String TABLE_VEHICLES       = "vehicles";
    public static final String VEHICLE_ID           = "vehicleId";
    public static final String VEHICLE_IDENTIFIER   = "vehicleIdentifier";


    //Parameters Table Routes
    public static final String TABLE_ROUTES = "routes";
    public static final String ROUTE_ID     = "routeId";
    public static final String ROUTE_NAME   = "routeName";
    public static final String ROUTE_DATA   = "routeData";
    public static final String ROUTE_ENABLED   = "enabled";

    //Parameters Table Session
    public static final String TABLE_SESSIONS   = "sessions";
    public static final String SESSION_ID       = "sessionId";
    public static final String SESSION_STATUS   = "status";
    public static final String IMEI             = "IMEI";
    //public static final String USER_ID     = "userId";
    //public static final String VEHICLE_ID  = "vehicleId";
    //public static final String ROUTE_ID     = "routeId";
    public static final String ROUTE_NAVIGATION = "routeNavigation";

    //Parameters Table Tracks
    public static final String TABLE_TRACKS = "tracks";
    public static final String TRACKS_ID    = "trackId";
    public static final String TRACKS_TYPE  = "type";
    //column session id
    public static final String TRACKS_DATA  = "tracks";


    //TABLE CREATION STATEMENTS -- INIT
    //Database creation SQL statement for table USERS for save user data
    private static final String CREATE_TABLE_LICENSE = "create table "
            + TABLE_LICENSE + "("
                + LICENSE_ID + " text primary key, "
                + LICENSE_CODE + " text not null unique, "
                //Timestamp is saved on UTC... it must be displayed on the page at localtime at will.
                + CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP);";

    //Database creation SQL statement for table USERS for save user data
    private static final String CREATE_TABLE_USERS = "create table "
            + TABLE_USERS + "("
                + USER_ID + " text primary key, "
                + USER_NAME + " text not null unique, "
                //Timestamp is saved on UTC... it must be displayed on the page at localtime at will.
                + CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP);";

    //Database creation SQL statement for table USERS for save user data
    private static final String CREATE_TABLE_VEHICLES = "create table "
            + TABLE_VEHICLES + "("
                + VEHICLE_ID + " text primary key, "
                + VEHICLE_IDENTIFIER + " text not null unique,"
                //Timestamp is saved on UTC... it must be displayed on the page at localtime at will.
                + CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP);";

    //Database creation SQL statement for table ROUTES for route data
    private static final String CREATE_TABLE_ROUTES = " create table "
            + TABLE_ROUTES + "("
                + ROUTE_ID + " text primary key, "
                + ROUTE_NAME + " text not null unique, "
                + ROUTE_DATA + " text not null, "
                + ROUTE_ENABLED + " text not null,"
                //Timestamp is saved on UTC... it must be displayed on the page at localtime at will.
                + CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP);";

    //Database creation for SQL statement for table SESSION, for record actual app session.
    private static final String CREATE_TABLE_SESSIONS = " create table "
            + TABLE_SESSIONS + " ("
                + SESSION_ID + " text primary key, "
                + SESSION_STATUS + " integer not null, "
                                //0 = not started
                                //1 = running
                                //2 = Canceled
                                //3 = finnish
                                //4 = finished and finished on server
                + IMEI + " text not null, "
                + USER_ID + " text not null, "
                + VEHICLE_ID + " text not null, "
                + ROUTE_ID + " text not null, "
                + ROUTE_NAVIGATION + " text not null,"
                //Timestamp is saved on UTC... it must be displayed on the page at localtime at will.
                + CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP);";
                //+ CREATED_AT + " DEFAULT (datetime('now','localtime')));";

    //Database creation SQL statement for table TRACKS to record data
    private static final String CREATE_TABLE_TRACKS =   "create table "
            + TABLE_TRACKS + " ("
                + TRACKS_ID + " text primary key, "
                + SESSION_ID + " text not null, "
                + TRACKS_TYPE + " text not null, "
                    // 1: normal track
                    // 2: event, GPS enabled
                    // 3: event, MAX VEL traspassed
                + TRACKS_DATA + " text not null, "
                //Timestamp is saved on UTC... it must be displayed on the page at localtime at will.
                + CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP);";

    //TABLE CREATION STATEMENTS -- END

    //DATABASE CREATION
    // Constructor for Context creation for the Database
    public SQL_Definition(Context context) {
        //super class
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // de null is a custom Cursor option that it's not used.
    }


    @Override
    public void onCreate(SQLiteDatabase database) {
        //SQLiteDatabase database
            database.execSQL(CREATE_TABLE_TRACKS);
            database.execSQL(CREATE_TABLE_USERS);
            database.execSQL(CREATE_TABLE_LICENSE);
            database.execSQL(CREATE_TABLE_VEHICLES);
            database.execSQL(CREATE_TABLE_ROUTES);
            database.execSQL(CREATE_TABLE_SESSIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        Log.w(SQL_Definition.class.getName(),
                "Upgrading database from version" + oldVersion + " to " + newVersion + "Which will destroy all data.");

        // on upgrade drop older tables
        //de SQLiteDatabase db
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LICENSE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VEHICLES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSIONS);

        // create new tables in the new DB version
        onCreate(db);

    }


    // DATABASE ADAPTERS
    // FUNCTIONS TO OPERATE TABLES -- INIT
    // INSERTS -- INIT
    // Insert value Users
    public void insertUser(HashMap<String, String> queryValues){
        // Open a database for reading and writing
        SQLiteDatabase database = this.getWritableDatabase();

        // Stores key value pairs being the column name and the data
        // ContentValues data type is needed because the database
        // requires its data type to be passed
        ContentValues values = new ContentValues();

        values.put(USER_ID, queryValues.get("userid"));
        values.put(USER_NAME, queryValues.get("username"));
//        Log.d(USER_NAME, queryValues.get("userName"));

        // Inserts the data in the form of ContentValues into the table name provided
        database.insert(TABLE_USERS,null, values);

        // Release the reference to the SQLiteDatabase object
        database.close();
    }

    // Insert value License
    public void insertLicense(String licenseCode){
        // Open a database for reading and writing
        SQLiteDatabase database = this.getWritableDatabase();

        // Stores key value pairs being the column name and the data
        // ContentValues data type is needed because the database
        // requires its data type to be passed
        ContentValues values = new ContentValues();

        values.put(LICENSE_ID, getColumnId());
        values.put(LICENSE_CODE, licenseCode);
        Log.d(LICENSE_CODE, "Inserting on license table value: "+licenseCode);

        // Inserts the data in the form of ContentValues into the table name provided
        database.insert(TABLE_LICENSE, null, values);

        // Release the reference to the SQLiteDatabase object
        database.close();
    }

    // Insert value in table Vehicles
    public void insertVehicle(HashMap<String, String> queryValues){
        // Open a database for reading and writing
        SQLiteDatabase database = this.getWritableDatabase();

        // Stores key value pairs being the column name and the data
        // ContentValues data type is needed because the database
        // requires its data type to be passed
        ContentValues values = new ContentValues();

        values.put(VEHICLE_ID, queryValues.get(VEHICLE_ID));
        values.put(VEHICLE_IDENTIFIER, queryValues.get(VEHICLE_IDENTIFIER));

        // Inserts the data in the form of ContentValues into the table name provided
        database.insert(TABLE_VEHICLES, null, values);
        Log.d(VEHICLE_IDENTIFIER, queryValues.get(VEHICLE_ID));

        // Release the reference to the SQLiteDatabase object
        database.close();
    }

    // Insert value in table Routes
    public void insertRoutes(HashMap<String, String> queryValues){
        // Open a database for reading and writing
        SQLiteDatabase database = this.getWritableDatabase();

        // Stores key value pairs being the column name and the data
        // ContentValues data type is needed because the database
        // requires its data type to be passed
        ContentValues values = new ContentValues();

        values.put(ROUTE_ID,queryValues.get("routeid"));
        values.put(ROUTE_NAME, queryValues.get("routename"));
        values.put(ROUTE_DATA, queryValues.get("routedata"));
        values.put(ROUTE_ENABLED, queryValues.get("routeenabled"));

        // Inserts the data in the form of ContentValues into the
        // table name provided


        database.insert(TABLE_ROUTES, null, values);

        // Release the reference to the SQLiteDatabase object
        database.close();
    }

    // Insert value in table Sessions
    public void insertSession(HashMap<String, String> queryValues){
        // Open a database for reading and writing
        SQLiteDatabase database = this.getWritableDatabase();

        // Stores key value pairs being the column name and the data
        // ContentValues data type is needed because the database
        // requires its data type to be passed
        ContentValues values = new ContentValues();
        values.put(SESSION_ID,getColumnId());
        values.put(SESSION_STATUS, Integer.parseInt(queryValues.get(SESSION_STATUS)));
        values.put(IMEI, queryValues.get(IMEI));
        values.put(USER_ID, queryValues.get(USER_ID));
        values.put(VEHICLE_ID, queryValues.get(VEHICLE_ID));
        values.put(ROUTE_ID,queryValues.get(ROUTE_ID));
        values.put(ROUTE_NAVIGATION,queryValues.get(ROUTE_NAVIGATION));

        // Inserts the data in the form of ContentValues into the
        // table name provided
        database.insert(TABLE_SESSIONS, null, values);

        // Release the reference to the SQLiteDatabase object
        database.close();
    }

    // Insert value in table Tracks
    public void insertTracks(HashMap<String, String> queryValues){
        // Open a database for reading and writing
        SQLiteDatabase database = this.getWritableDatabase();

        // Stores key value pairs being the column name and the data
        // ContentValues data type is needed because the database
        // requires its data type to be passed
        ContentValues values = new ContentValues();

        values.put(TRACKS_ID,getColumnId());
        values.put(SESSION_ID, queryValues.get(SESSION_ID));
        values.put(TRACKS_TYPE, queryValues.get(TRACKS_TYPE));
        // 1: velocity
        // 2: event
        values.put(TRACKS_DATA, queryValues.get(TRACKS_DATA));


        // Inserts the data in the form of ContentValues into the
        // table name provided
        int activeSession = checkSessionActive();
        if (activeSession == 1 ){
            database.insert(TABLE_TRACKS, null, values);
        };

        // Release the reference to the SQLiteDatabase object
        database.close();
    }
    // INSERTS -- END

    // UPDATES -- INIT
    // Update value Users
    public int updateUser(HashMap<String, String> queryValues){
        // Open a database for reading and writing
        SQLiteDatabase database = this.getWritableDatabase();

        // Stores key value pairs being the column name and the data
        // ContentValues data type is needed because the database
        // requires its data type to be passed
        ContentValues values = new ContentValues();

        values.put("userName", queryValues.get("userName"));

        // update(TableName, ContentValueForTable, WhereClause, ArgumentForWhereClause)
        return database.update(TABLE_USERS, values, "userId" + " = ?", new String[] { queryValues.get("userId") });
    }

    // Update value in table Vehicles
    public int updateVehicle(HashMap<String, String> queryValues){
        // Open a database for reading and writing
        SQLiteDatabase database = this.getWritableDatabase();

        // Stores key value pairs being the column name and the data
        // ContentValues data type is needed because the database
        // requires its data type to be passed
        ContentValues values = new ContentValues();

        values.put("vehicleIdentifier", queryValues.get("vehicleIdentifier"));

        // update(TableName, ContentValueForTable, WhereClause, ArgumentForWhereClause)
        return database.update(TABLE_VEHICLES, values, "vehicleId" + " = ?", new String[] { queryValues.get("vehicleId") });
    }

    // Update value in table Routes
    public int updateRoutes(HashMap<String, String> queryValues){
        // Open a database for reading and writing
        SQLiteDatabase database = this.getWritableDatabase();

        // Stores key value pairs being the column name and the data
        // ContentValues data type is needed because the database
        // requires its data type to be passed
        ContentValues values = new ContentValues();

        values.put("routeName", queryValues.get("routeName"));
        values.put("routeData", queryValues.get("routeData"));
        values.put("routeEnabled", queryValues.get("routeEnabled"));

        // update(TableName, ContentValueForTable, WhereClause, ArgumentForWhereClause)
        return database.update(TABLE_ROUTES, values, "routeId" + " = ?", new String[] { queryValues.get("routeId") });
    }

    // update value in table Sessions
    public int updateSession(HashMap<String, String> queryValues){
        // Open a database for reading and writing
        SQLiteDatabase database = this.getWritableDatabase();

        // Stores key value pairs being the column name and the data
        // ContentValues data type is needed because the database
        // requires its data type to be passed
        ContentValues values = new ContentValues();

        values.put(SESSION_STATUS, queryValues.get(SESSION_STATUS));
        //values.put("imei", queryValues.get("imei"));
        values.put(SESSION_ID, queryValues.get(SESSION_ID));
        values.put(VEHICLE_ID, queryValues.get(VEHICLE_ID));
        values.put(ROUTE_ID, queryValues.get(ROUTE_ID));
        values.put(ROUTE_NAVIGATION, queryValues.get(ROUTE_NAVIGATION));

        // update(TableName, ContentValueForTable, WhereClause, ArgumentForWhereClause)
        return database.update(TABLE_SESSIONS, values, "sessionId" + " = ?", new String[] { queryValues.get(SESSION_ID) });
    }

    // update value in table Sessions
    public int updateSessionRoute(HashMap<String, String> queryValues){
        // Open a database for reading and writing
        SQLiteDatabase database = this.getWritableDatabase();

        // Stores key value pairs being the column name and the data
        // ContentValues data type is needed because the database
        // requires its data type to be passed
        ContentValues values = new ContentValues();

        values.put(SESSION_ID, queryValues.get(SESSION_ID));
        values.put(ROUTE_NAVIGATION, queryValues.get(ROUTE_NAVIGATION));

        // update(TableName, ContentValueForTable, WhereClause, ArgumentForWhereClause)
        return database.update(TABLE_SESSIONS, values, "sessionId" + " = ?", new String[] { queryValues.get(SESSION_ID) });
    }

    // update value in table Sessions
    public void updateSessionIdFinnishedAndEmailed(String id){
        // Open a database for reading and writing
        SQLiteDatabase database = this.getWritableDatabase();

        String updateQuery = "UPDATE  " + TABLE_SESSIONS + " SET " + SESSION_STATUS + " = 4 "  + " where " + SESSION_ID + " ='"+ id +"' ";
        database.execSQL(updateQuery);
    }

    // Update value in table Tracks
    public int updateTrack(HashMap<String, String> queryValues){
        // Open a database for reading and writing
        SQLiteDatabase database = this.getWritableDatabase();

        // Stores key value pairs being the column name and the data
        // ContentValues data type is needed because the database
        // requires its data type to be passed
        ContentValues values = new ContentValues();

        values.put("trackType", queryValues.get("trackType"));
        values.put("sessionId", queryValues.get("sessionId"));
        values.put("trackData", queryValues.get("trackData"));

        // update(TableName, ContentValueForTable, WhereClause, ArgumentForWhereClause)
        return database.update(TABLE_TRACKS, values, "trackId" + " = ?", new String[] { queryValues.get("trackId") });
    }
    // UPDATES -- END

    // DELETES -- INIT
    // Delete value in table Users
    public void deleteUser(String id) {

        // Open a database for reading and writing
        SQLiteDatabase database = this.getWritableDatabase();

        String deleteQuery = "DELETE FROM  " + TABLE_USERS + " where " + USER_ID + " ='"+ id +"' ";

        // Executes the query provided as long as the query isn't a select
        // or if the query doesn't return any data
        database.execSQL(deleteQuery);
    }

    // Delete value in table Vehicles
    public void deleteVehicle(String id) {

        // Open a database for reading and writing
        SQLiteDatabase database = this.getWritableDatabase();

        String deleteQuery = "DELETE FROM  " + TABLE_VEHICLES + " where " + VEHICLE_ID + " ='"+ id +"' ";

        // Executes the query provided as long as the query isn't a select
        // or if the query doesn't return any data
        database.execSQL(deleteQuery);
    }

    // Delete value in table routes
    public void deleteRoute(String id) {

        // Open a database for reading and writing
        SQLiteDatabase database = this.getWritableDatabase();

        String deleteQuery = "DELETE FROM  " + TABLE_ROUTES + " where " + ROUTE_ID + " ='"+ id +"' ";

        // Executes the query provided as long as the query isn't a select
        // or if the query doesn't return any data
        database.execSQL(deleteQuery);
    }

    // Delete value in table sessions
    public void deleteSession(String id) {

        // Open a database for reading and writing
        SQLiteDatabase database = this.getWritableDatabase();

        String deleteQuery = "DELETE FROM  " + TABLE_SESSIONS + " where " + SESSION_ID + " ='"+ id +"' ";

        // Executes the query provided as long as the query isn't a select
        // or if the query doesn't return any data
        database.execSQL(deleteQuery);
    }

    // Delete value in table tracks
    public void deleteTrack(String id) {

        // Open a database for reading and writing
        SQLiteDatabase database = this.getWritableDatabase();

        String deleteQuery = "DELETE FROM  " + TABLE_TRACKS + " where " + TRACKS_ID + " ='"+ id +"' ";

        // Executes the query provided as long as the query isn't a select
        // or if the query doesn't return any data
        database.execSQL(deleteQuery);
    }
    // DELETES -- END

    // SELECTS ALL-- INIT
    // Selects all data from table users
    public ArrayList<HashMap<String, String>> getAllUsers() {

        // ArrayList that contains every row in the database
        // and each row key / value stored in a HashMap
        ArrayList<HashMap<String, String>> usersArrayList;
        usersArrayList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM " + TABLE_USERS;

        // Open a database for reading only
        SQLiteDatabase database = this.getReadableDatabase();

        // Cursor provides read and write access for the
        // data returned from a database query

        // rawQuery executes the query and returns the result as a Cursor
        Cursor cursor = database.rawQuery(selectQuery, null);

        // Move to the first row
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> usersMap = new HashMap<String, String>();

                // Store the key / value pairs in a HashMap
                // Access the Cursor data by index that is in the same order
                // as used when creating the table
                usersMap.put("userId", cursor.getString(0));
                usersMap.put("userName", cursor.getString(1));

                usersArrayList.add(usersMap);
            } while (cursor.moveToNext()); // Move Cursor to the next row
        }

        // return contact list
        return usersArrayList;
    }

    public ArrayList<String> getUsersLabels(){
        ArrayList<String> labels = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_USERS ;

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        database.close();

        // returning lables
        return labels;
    }

    public ArrayList<String> getUsersLabels2(){
        ArrayList<String> labels = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_USERS ;

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        database.close();

        // returning lables
        return labels;
    }

    public List<String> getVehiclesLabels(){
        List<String> labels = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_VEHICLES;

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        database.close();

        // returning lables
        return labels;
    }

    public List<String> getRoutesLabels(){
        List<String> labels = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ROUTES;

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        database.close();

        // returning lables
        return labels;
    }

    // Selects all data from table vehicles
    public ArrayList<HashMap<String, String>> getAllVehicles() {

        // ArrayList that contains every row in the database
        // and each row key / value stored in a HashMap
        ArrayList<HashMap<String, String>> vehiclesArrayList;
        vehiclesArrayList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM " + TABLE_VEHICLES;

        // Open a database for reading only
        SQLiteDatabase database = this.getReadableDatabase();

        // Cursor provides read and write access for the
        // data returned from a database query

        // rawQuery executes the query and returns the result as a Cursor
        Cursor cursor = database.rawQuery(selectQuery, null);

        // Move to the first row

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> vehiclesMap = new HashMap<String, String>();

                // Store the key / value pairs in a HashMap
                // Access the Cursor data by index that is in the same order
                // as used when creating the table
                vehiclesMap.put(VEHICLE_ID, cursor.getString(0));
                vehiclesMap.put(VEHICLE_IDENTIFIER, cursor.getString(1));

                vehiclesArrayList.add(vehiclesMap);
            } while (cursor.moveToNext()); // Move Cursor to the next row
        }

        // return contact list
        return vehiclesArrayList;
    }

    // Selects all data from table routes
    public ArrayList<HashMap<String, String>> getAllRoutes() {

        // ArrayList that contains every row in the database
        // and each row key / value stored in a HashMap
        ArrayList<HashMap<String, String>> routesArrayList;
        routesArrayList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM " + TABLE_ROUTES;

        // Open a database for reading only
        SQLiteDatabase database = this.getReadableDatabase();

        // Cursor provides read and write access for the
        // data returned from a database query

        // rawQuery executes the query and returns the result as a Cursor
        Cursor cursor = database.rawQuery(selectQuery, null);

        // Move to the first row

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> routesMap = new HashMap<String, String>();

                // Store the key / value pairs in a HashMap
                // Access the Cursor data by index that is in the same order
                // as used when creating the table
                routesMap.put(ROUTE_ID, cursor.getString(0));
                routesMap.put(ROUTE_NAME, cursor.getString(1));
                routesMap.put(ROUTE_DATA, cursor.getString(2));

                routesArrayList.add(routesMap);
            } while (cursor.moveToNext()); // Move Cursor to the next row
        }

        // return contact list
        return routesArrayList;
    }

    // Selects all data from table sessions
    public ArrayList<HashMap<String, String>> getAllSessions() {

        // ArrayList that contains every row in the database
        // and each row key / value stored in a HashMap
        ArrayList<HashMap<String, String>> sessionsArrayList;
        sessionsArrayList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM " + TABLE_SESSIONS;

        // Open a database for reading only
        SQLiteDatabase database = this.getReadableDatabase();

        // Cursor provides read and write access for the
        // data returned from a database query

        // rawQuery executes the query and returns the result as a Cursor
        Cursor cursor = database.rawQuery(selectQuery, null);

        // Move to the first row

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> sessionsMap = new HashMap<String, String>();

                // Store the key / value pairs in a HashMap
                // Access the Cursor data by index that is in the same order
                // as used when creating the table

                sessionsMap.put(SESSION_ID, cursor.getString(0));
                sessionsMap.put(SESSION_STATUS, cursor.getString(1));
                sessionsMap.put(IMEI, cursor.getString(2));
                sessionsMap.put(USER_ID, cursor.getString(3));
                sessionsMap.put(VEHICLE_ID, cursor.getString(4));

                sessionsArrayList.add(sessionsMap);
            } while (cursor.moveToNext()); // Move Cursor to the next row
        }

        // return contact list
        return sessionsArrayList;
    }

    // Selects all data from table sessions
    public ArrayList<HashMap<String, String>> getAllTracks() {

        // ArrayList that contains every row in the database
        // and each row key / value stored in a HashMap
        ArrayList<HashMap<String, String>> tracksArrayList;
        tracksArrayList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM " + TABLE_SESSIONS;

        // Open a database for reading only
        SQLiteDatabase database = this.getReadableDatabase();

        // Cursor provides read and write access for the
        // data returned from a database query

        // rawQuery executes the query and returns the result as a Cursor
        Cursor cursor = database.rawQuery(selectQuery, null);

        // Move to the first row

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> tracksMap = new HashMap<String, String>();
                // Store the key / value pairs in a HashMap
                // Access the Cursor data by index that is in the same order
                // as used when creating the table
                tracksMap.put(TRACKS_ID, cursor.getString(0));
                tracksMap.put(SESSION_ID, cursor.getString(1));
                tracksMap.put(TRACKS_TYPE, cursor.getString(2));
                tracksMap.put(TRACKS_DATA, cursor.getString(3));

                tracksArrayList.add(tracksMap);
            } while (cursor.moveToNext()); // Move Cursor to the next row
        }

        // return contact list
        return tracksArrayList;
    }
    // SELECTS ALL-- END

    // SELECTS SPECIFIC VALUES -- INIT
    // Select specific user in table users from user ID
    public HashMap<String, String> getUserById(String id) {
        HashMap<String, String> userMap = new HashMap<String, String>();

        // Open a database for reading only
        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_USERS
                            + " where " + USER_ID +" ='" + id + "' ";

        // rawQuery executes the query and returns the result as a Cursor
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                userMap.put(USER_NAME, cursor.getString(1));

            } while (cursor.moveToNext());
        }
        return userMap;
    }

    public HashMap<String, String> getLicenseCode() {
        HashMap<String, String> LicenseMap = new HashMap<String, String>();

        // Open a database for reading only
        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_LICENSE
                + " limit 1 ";

        // rawQuery executes the query and returns the result as a Cursor
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                LicenseMap.put(LICENSE_CODE, cursor.getString(1));

            } while (cursor.moveToNext());
        }
        return LicenseMap;
    }

    public String getUserIdByName(String userName){

        String id = "";
        // Open a database for reading only
        SQLiteDatabase database = this.getReadableDatabase();

        //Select ID query
        String selectQuery = "Select " + USER_ID + " from " +TABLE_USERS
                           + " where " + USER_NAME + " ='" + userName + "' ";
        // rawQuery executes the query and returns the result as a Cursor
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getString(0);

            } while (cursor.moveToNext());
        }
        return id;
    }

    public String getVehicleIdByName(String vehicleIdenti){

        String id = "";
        // Open a database for reading only
        SQLiteDatabase database = this.getReadableDatabase();

        //Select ID query
        String selectQuery = "Select " + VEHICLE_ID + " from " +TABLE_VEHICLES
                + " where " + VEHICLE_IDENTIFIER + " ='" + vehicleIdenti + "' ";
        // rawQuery executes the query and returns the result as a Cursor
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getString(0);

            } while (cursor.moveToNext());
        }
        return id;
    }

    public String getRouteIdByName(String routeName){

        String id = "";
        // Open a database for reading only
        SQLiteDatabase database = this.getReadableDatabase();

        //Select ID query
        String selectQuery = "Select " + ROUTE_ID + " from " + TABLE_ROUTES
                + " where " + ROUTE_NAME + " ='" + routeName + "' ";
        // rawQuery executes the query and returns the result as a Cursor
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getString(0);

            } while (cursor.moveToNext());
        }
        return id;
    }

    public String getRouteDataById(String routeId){

        String routeData = "";
        // Open a database for reading only
        SQLiteDatabase database = this.getReadableDatabase();

        //Select ID query
        String selectQuery = "Select " + ROUTE_DATA+ " from " + TABLE_ROUTES
                + " where " + ROUTE_ID + " ='" + routeId + "' ";
        // rawQuery executes the query and returns the result as a Cursor
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                routeData = cursor.getString(2);

            } while (cursor.moveToNext());
        }
        return routeData;
    }

    // Select specific vehicle information in table vehicles from vehicle ID
    public HashMap<String, String> getVehicleById(String id) {
        HashMap<String, String> vehicleMap = new HashMap<String, String>();

        // Open a database for reading only
        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_VEHICLES
                + " where " + VEHICLE_ID +" ='" + id + "' ";

        // rawQuery executes the query and returns the result as a Cursor
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                vehicleMap.put(VEHICLE_IDENTIFIER, cursor.getString(1));

            } while (cursor.moveToNext());
        }
        return vehicleMap;
    }

    // Select specific route in table routes from route ID
    public HashMap<String, String> getRouteById(String id) {
        HashMap<String, String> routeMap = new HashMap<String, String>();

        // Open a database for reading only
        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_ROUTES
                + " where " + ROUTE_ID +" ='" + id + "' ";

        // rawQuery executes the query and returns the result as a Cursor
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                routeMap.put(ROUTE_NAME, cursor.getString(1));
                routeMap.put(ROUTE_DATA, cursor.getString(2));

            } while (cursor.moveToNext());
        }
        return routeMap;
    }

    // Select specific session in table session from session ID
    public HashMap<String, String> getSessionById(String id) {
        HashMap<String, String> sessionMap = new HashMap<String, String>();

        // Open a database for reading only
        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_SESSIONS
                + " where " + SESSION_ID +" ='" + id + "' ";

        // rawQuery executes the query and returns the result as a Cursor
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                sessionMap.put(SESSION_STATUS, cursor.getString(1));
                sessionMap.put(IMEI, cursor.getString(2));
                sessionMap.put(USER_ID, cursor.getString(3));
                sessionMap.put(VEHICLE_ID, cursor.getString(4));

            } while (cursor.moveToNext());
        }
        return sessionMap;
    }

    // Select Active session in table session from session status = Active (value=1)
    public int checkSessionActive() {
        HashMap<String, String> sessionMap = new HashMap<String, String>();

        // Open a database for reading only
        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT "+ SESSION_STATUS + " FROM " + TABLE_SESSIONS
//                + " where " + SESSION_STATUS +" ='1' "
//                + " or " + SESSION_STATUS +" ='3' "
//                + " or " + SESSION_STATUS +" ='4' LIMIT 1 ";
                + " where " + SESSION_STATUS +" ='1'  LIMIT 1 ";


        // rawQuery executes the query and returns the result as a Cursor
        Cursor cursor = database.rawQuery(selectQuery, null);
//        String session = database.execSQL(selectQuery);
        if (cursor.moveToFirst()) {
            do {
                sessionMap.put("value",cursor.getString(0));
//                sessionMap.put(SESSION_STATUS, cursor.getString(1));
//                sessionMap.put(IMEI, cursor.getString(2));
//                sessionMap.put(USER_ID, cursor.getString(3));
//                sessionMap.put(VEHICLE_ID, cursor.getString(4));

            } while (cursor.moveToNext());
        }
        int resultSessionStatus;

        if (cursor.getCount()!=0){
            resultSessionStatus = Integer.valueOf(sessionMap.get("value"));
        }
        else {
            resultSessionStatus= 0;
        }

        return resultSessionStatus;
    }

    // Select Active session in table session from session status = Active (value=1)
    public HashMap<String, String> getSessionActive() {
        HashMap<String, String> sessionMap = new HashMap<String, String>();

        // Open a database for reading only
        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_SESSIONS
//                + " where " + SESSION_STATUS +" ='1' "
//                + " or " + SESSION_STATUS +" ='4' "
//                + " or " + SESSION_STATUS +" ='3' LIMIT 1 "
                + " where " + SESSION_STATUS +" ='1' LIMIT 1 "
                ;

        // rawQuery executes the query and returns the result as a Cursor
        Cursor cursor = database.rawQuery(selectQuery, null);
        int checkQuantityActiveSession = cursor.getCount();

        if (checkQuantityActiveSession == 1) {
            if (cursor.moveToFirst()) {
                do {
                    sessionMap.put(SESSION_ID, cursor.getString(0));
                    sessionMap.put(SESSION_STATUS, cursor.getString(1));
                    sessionMap.put(IMEI, cursor.getString(2));
                    sessionMap.put(USER_ID, cursor.getString(3));
                    sessionMap.put(VEHICLE_ID, cursor.getString(4));
                    sessionMap.put(ROUTE_ID, cursor.getString(5));
                    sessionMap.put(ROUTE_NAVIGATION,cursor.getString(6));
                    sessionMap.put(CREATED_AT, cursor.getString(7));

                } while (cursor.moveToNext());
                //Log.d("sessionMap", String.valueOf(sessionMap));
            }
        }
        else {
            //update to all values to status <> '0'
            //Log.d("sessionMap Session Error:", String.valueOf(sessionMap));
        }

        //Log.d("sessionMap:", String.valueOf(sessionMap));

        return sessionMap;
    }

    // Select Active session in table session from session status = Active (value=1)
    public String getTracksCount() {
        HashMap<String, String> countMap = new HashMap<String, String>();

        // Open a database for reading only
        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT count() FROM " + TABLE_TRACKS;

        // rawQuery executes the query and returns the result as a Cursor
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                countMap.put("count",cursor.getString(0));
//                sessionMap.put(SESSION_STATUS, cursor.getString(1));
//                sessionMap.put(IMEI, cursor.getString(2));
//                sessionMap.put(USER_ID, cursor.getString(3));
//                sessionMap.put(VEHICLE_ID, cursor.getString(4));

            } while (cursor.moveToNext());
        }
        String resultTracksCount;
        if (Integer.valueOf(countMap.get("count")).equals(0)){
            resultTracksCount = "Synchronized";
        } else {
            resultTracksCount = Integer.valueOf(countMap.get("count")).toString() + " Left";
        }

        return resultTracksCount;
    }




    // SELECTS SPECIFIC VALUES -- END


    private String getColumnId() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyyMMddHHmmssSSS", Locale.getDefault());
        Date date = new Date();
//        int IMEInumber = Integer.parseInt(MainActivity.IMEI);
//
//        int idValue = Integer.parseInt(dateFormat.format(date).toString());
//        idValue = IMEInumber + idValue;

        String idValue = MainActivity.IMEI+dateFormat.format(date).toString();

        return idValue;
    }

    public List<String> getTracksForSync(String rowLimit){
        List<String> tracks = new ArrayList<String>();

        // Select All Query
        //String selectQuery = "SELECT  * FROM " + TABLE_VEHICLES;
        String selectQuery =   "SELECT '{\"trackid\":\"'   || trackid    || "+
                                    "'\",\"sessionId\":\"' || sessionid  || "+
                                    "'\",\"type\":\"'      || type       || "+
                                    "'\",\"trackdata\":'   || tracks     || "+
                                    "',\"created_at\":\"'  || created_at || "+
                                    "'\"}' "+
                                " FROM "+ TABLE_TRACKS +
                                " limit "+ rowLimit;

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                tracks.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        database.close();

        // returning tracks
        return tracks;
    }

    // Selects all data from table sessions
    public List<String>  getAllSessionsJson() {

        List<String> sessions = new ArrayList<String>();

        // Select All Query
        String selectQuery =   "SELECT '{\"sessionId\":\"'   || sessionId    || "+
                "'\",\"status\":\"' || status  || "+
                "'\",\"IMEI\":\"'      || IMEI       || "+
                "'\",\"userId\":\"'   || userId     || "+
                "'\",\"vehicleId\":\"'   || vehicleId     || "+
                "'\",\"routeId\":\"'   || routeId     || "+
              //ss  "'\",\"routeNavigation\":'   || routeNavigation     || "+
                "'\",\"created_at\":\"'  || created_at || "+
                "'\"}' "+
                " FROM "+ TABLE_SESSIONS +" ";

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                sessions.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        database.close();

        // returning tracks
        return sessions;
        }
    // FUNCTIONS TO OPERATE TABLES -- END
}
