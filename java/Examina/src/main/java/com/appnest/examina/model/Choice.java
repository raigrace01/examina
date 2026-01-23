package com.appnest.examina.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.firebase.database.PropertyName;

public class Choice {
    @PropertyName("choice")
    public String choice;

    @PropertyName("isAnswer")
    public boolean isAnswer;

    @PropertyName("uuid")
    public String uuid;




}