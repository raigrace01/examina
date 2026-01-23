package com.appnest.examina.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.firebase.database.PropertyName;

import java.util.List;

public class Explanation {
    @PropertyName("html_text")
    public String html_text;

    @PropertyName("images")
    public List<String> images;



}