package com.kanibalv.app;

/**
 * Created by kanibal on 1/16/14.
 */
public class user_data {
    private long id;
    private String userdata;

    public user_data(){

    }

    public user_data(long id){
        this.id = id;
    }

    public user_data(long id, String userdata){
        this.id = id;
        this.userdata = userdata;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setUserdata(String userdata) {
        this.userdata = userdata;
    }

    public String getUserdata() {
        return userdata;
    }
}
