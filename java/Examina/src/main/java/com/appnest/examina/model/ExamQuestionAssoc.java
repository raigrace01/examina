package com.appnest.examina.model;

import com.google.cloud.firestore.DocumentReference;

import java.util.List;

public class ExamQuestionAssoc {
    public List<QuestionShortInfo> data;
    public DocumentReference exam_ref;
}
