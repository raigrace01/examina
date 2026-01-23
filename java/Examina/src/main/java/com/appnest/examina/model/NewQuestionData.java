package com.appnest.examina.model;


import com.google.cloud.firestore.DocumentReference;
import com.google.firebase.database.PropertyName;

import java.util.List;

public class NewQuestionData {
    public BasicInfo basic_info;
    public List<Choice> choices;
    public Explanation explanation;
    public NewOtherInfo other_info;
    public DocumentReference question_ref;
}