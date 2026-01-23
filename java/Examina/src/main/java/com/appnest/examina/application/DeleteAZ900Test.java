package com.appnest.examina.application;

import com.appnest.examina.model.ExamQuestionAssoc;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class DeleteAZ900Test {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        FileInputStream serviceAccount = new FileInputStream("D:/ryan/examinaServiceAccountKey.json");
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setProjectId("examina-vy0zd6")
                .build();
        FirebaseApp.initializeApp(options);
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRefaz900=null;
        List<QueryDocumentSnapshot> documents = db.collection("exam").get().get().getDocuments();
        for(QueryDocumentSnapshot doc:documents){
            if(doc.get("exam_code").equals("AZ-900")){
                docRefaz900 = doc.getReference();
            }
        }


        // Create a Map with the field to delete
        /*
        Map<String, Object> updates = new HashMap<>();
        updates.put("questions", FieldValue.delete());
        ApiFuture<WriteResult> writeResult = docRefaz900.update(updates);

         */
        try {
            ApiFuture<WriteResult> future = docRefaz900.update("questions", FieldValue.delete());
            WriteResult writeResult = future.get();
            System.out.println("Update time:" + writeResult.getUpdateTime());

            future=docRefaz900.update("question_short_info", FieldValue.delete());
            writeResult = future.get();
            System.out.println("Update time:" + writeResult.getUpdateTime());

            DocumentSnapshot docExam = docRefaz900.get().get();
            DocumentReference docRefQuestionAssoc = (DocumentReference) docExam.get("question_list_assoc_ref");
            future = docRefQuestionAssoc.update("data", FieldValue.delete());
            writeResult = future.get();
            System.out.println("Update time:" + writeResult.getUpdateTime());


        }
        catch(Exception e){
            System.out.println(e);
        }



        List<QueryDocumentSnapshot> questionDocs = db.collection("question").get().get().getDocuments();

        for(QueryDocumentSnapshot doc:questionDocs){
            System.out.println("deleting Questions");
            WriteResult wr = doc.getReference().delete().get();
            System.out.println(wr.getUpdateTime());
        }
    }
}
