package com.appnest.examina.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.firebase.database.PropertyName;

import java.util.List;

public class ExamQuestionData {
    @PropertyName("basic_info")
    public BasicInfo basic_info;

    @PropertyName("choices")
    public List<Choice> choices;

    @PropertyName("explanation")
    public Explanation explanation;


    @PropertyName("other_info")
    public OtherInfo other_info;

    @PropertyName("question_ref")
    public String question_ref;


}