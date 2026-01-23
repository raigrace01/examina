package com.appnest.examina.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.firebase.database.PropertyName;

import java.util.List;

public class BasicInfo {
    @PropertyName("difficulty")
    public String difficulty;

    // The JSON uses "question_data" again here for the text array
    @PropertyName("question_data")
    public List<QuestionDetail> question_data;

    @PropertyName("question_type")
    public String question_type;


}