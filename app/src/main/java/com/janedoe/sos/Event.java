package com.janedoe.sos;

import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by demouser on 8/4/16.
 */
public class Event {

    public String key;
    public String location;
    public String time;
    public String fileUrl;
    public String message;
    public String userId = FirebaseInstanceId.getInstance().getToken();
    public String isAccepted = "false";

    public Event() {} // not used but necessary for firebase

    public Event(String location, String time, String message, String fileUrl) {
        this.location = location;
        this.time = time;
        this.message = message;
        this.fileUrl = fileUrl;
    }

}
