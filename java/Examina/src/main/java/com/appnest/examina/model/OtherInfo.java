package com.appnest.examina.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.firebase.database.PropertyName;

import java.sql.Timestamp;

public class OtherInfo {
    @PropertyName("data_added")
    public String data_added;

    @PropertyName("date_modified")
    public String date_modified;

    @PropertyName("source")
    public String source;

    @PropertyName("status")
    public String status;

    @PropertyName("sub_topic")
    public String sub_topic;

    @PropertyName("topic")
    public String topic;

    @PropertyName("uuid")
    public String uuid;





    // Add other getters/setters as needed for dates/source/status
}