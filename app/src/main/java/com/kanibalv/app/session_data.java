package com.kanibalv.app;

/**
 * Created by kanibal on 1/15/14.
 */
public class session_data {
    private long id;
    private int status;
    private String session;


    //Constructors
    public session_data(){

    }

    public session_data(long id, int status){
        this.id = id;
        this.status = status;
    }

    public session_data(long id, int status, String session){
        this.id = id;
        this.status = status;
        this.session = session;
    }

    //Columns
    //ID
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    //Status data
    public int getStatus(){
        return status;
    }

    public void setStatus(int status){
        this.status = status;
    }

    //Session data
    public String getSession() {
        return session;
    }

    public String setSession(String session) {
        this.session = session;
        return null;
    }

    //Not totally necessary
    @Override
    public String toString() {
        return session ;
    }



}
