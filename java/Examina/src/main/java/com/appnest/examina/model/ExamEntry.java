

package com.appnest.examina.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.firestore.DocumentReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

public class ExamEntry {
    /*
    @JsonProperty("exam_ref")
    public String examRef;
*/
    @PropertyName("exam_ref")
    public String exam_ref;





    public void setQuestionData(ExamQuestionData question_data) {
        this.question_data = question_data;
    }

    @PropertyName("question_data")
    public ExamQuestionData question_data;


}