package com.kanibalv.app;

/**
 * Created by kanibal on 1/15/14.
 */
public class tracks_data {
    private long id;
    //private String tracks;
    private String tracks_type;
    private long session_id;
    private String tracks_data;

    public tracks_data(){
        
    }

    public tracks_data(long id){
        this.id = id;
    }

    public tracks_data(long id, String tracks_data){
        this.id = id;
        this.tracks_data =  tracks_data;
    }

    public tracks_data(long id, String tracks_data, long session_id){
        this.id = id;
        this.tracks_data = tracks_data;
        this.session_id = session_id;
    }

    public tracks_data(long id, String tracks_data, long session_id, String tracks_type){
        this.id = id;
        this.tracks_data = tracks_data;
        this.session_id = session_id;
        this.tracks_type = tracks_type;
    }

    // ID
    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setSession_id(long session_id){
        this.session_id = session_id;
    }

    public long getSession_id() {
        return session_id;
    }

    public void setTracks_type(String tracks_type){
        this.tracks_type = tracks_type;
    }

    public String getTracks_type() {
        return tracks_type;
    }

    public void setTracks_data(String tracks_data){
        this.tracks_data = tracks_data;
    }

    public String getTracks_data() {
        return tracks_data;
    }

}

