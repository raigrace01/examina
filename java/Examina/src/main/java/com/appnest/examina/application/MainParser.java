package com.appnest.examina.application;

import com.appnest.breedit.application.AddToAdminView;
import com.appnest.examina.model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.units.qual.N;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.*;
import java.util.concurrent.ExecutionException;


public class MainParser {
    private static final Logger logger = LogManager.getLogger(MainParser.class);
    private static Firestore db;
    private String QUESTION_STATUS = "published";
    public static void main(String[] args) throws IOException {

        MainParser parser = new MainParser();
        FileInputStream serviceAccount = new FileInputStream("D:/ryan/examinaServiceAccountKey.json");
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setProjectId("examina-vy0zd6")
                .build();
        FirebaseApp.initializeApp(options);
        db = FirestoreClient.getFirestore();
        parser.insertEntries("batch_1.txt");
        parser.insertEntries("batch_2.txt");
        parser.insertEntries("batch_3.txt");
        parser.insertEntries("batch_4.txt");
        parser.insertEntries("batch_5.txt");
        parser.insertEntries("batch_6.txt");
        parser.insertEntries("batch_7.txt");
        parser.insertEntries("batch_8.txt");
        parser.insertEntries("batch_9.txt");
        parser.insertEntries("batch_10.txt");

    }



    public void insertEntries(String filename){
        logger.info("log level {}",logger.getLevel());
        logger.info("Starting application...");
        ObjectMapper mapper = new ObjectMapper();
        // Prevents crashing if the JSON contains fields not in your Java classes
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            // Ensure this matches your actual file name
            File file = new File("D:\\ryan\\examina\\az900\\"+filename);

            // Parse the JSON array into a List of ExamEntry objects
            List<ExamEntry> examEntries = mapper.readValue(file, new TypeReference<List<ExamEntry>>(){});

            logger.info("Connected to firebase db {}",db.getOptions().getDatabaseId());
            List<QueryDocumentSnapshot> documents = db.collection("exam").get().get().getDocuments();
            DocumentReference docRefaz900=null;
            for(QueryDocumentSnapshot doc:documents){
                if(doc.get("exam_code").equals("AZ-900")){
                    docRefaz900 = doc.getReference();
                }
            }
            System.out.println(docRefaz900);





            for (ExamEntry entry : examEntries) {
                System.out.println("------------------------------------------------");
                System.out.println("Exam Ref: " + entry.exam_ref);
                //entry.getQuestionData().getOtherInfo().getUuid();
                ExamQuestionData question_data = entry.question_data;






                DocumentReference newDocRef = db.collection("question").document();
                NewQuestion question = new NewQuestion();

                /*
                String rawPath = "/exam/B3kxxPOW6dzCJktiiLME";
                String path = rawPath.startsWith("/") ? rawPath.substring(1) : rawPath;
                DocumentReference docRef = db.document(path); */
                question.exam_ref=docRefaz900;


                //question.question_data = question_data;
                OtherInfo otherInfo = question_data.other_info;;
                //set new otherinfo
                NewOtherInfo newOtherInfo = new NewOtherInfo();
                newOtherInfo.data_added = Timestamp.of(new Date());
                newOtherInfo.date_modified=Timestamp.of(new Date());
                newOtherInfo.source = otherInfo.source;
                newOtherInfo.status=QUESTION_STATUS;
                newOtherInfo.sub_topic= otherInfo.sub_topic;
                newOtherInfo.topic=otherInfo.topic;
                newOtherInfo.uuid=UUID.randomUUID().toString();



                NewQuestionData newQuestionData = new NewQuestionData();
                newQuestionData.basic_info = question_data.basic_info;
                newQuestionData.explanation = question_data.explanation;
                newQuestionData.other_info = newOtherInfo;
                newQuestionData.choices = question_data.choices;

                //override choices
                List<Choice> choices = new ArrayList<>();
                for(Choice choice:newQuestionData.choices){
                    choice.uuid=UUID.randomUUID().toString();
                    choices.add(choice);
                }
                newQuestionData.choices=choices;

                /*
                String rawPath=question_data.question_ref;
                String path = rawPath.startsWith("/") ? rawPath.substring(1) : rawPath; */

                newQuestionData.question_ref = newDocRef; //set the docRef as question_ref
                question.question_data = newQuestionData;

                ApiFuture<WriteResult> future = newDocRef.set(question);
                WriteResult wr = future.get();

                System.out.println(wr.toString());

                QuestionShortInfo qs = new QuestionShortInfo();
                qs.ref = newDocRef;
                qs.topic=newQuestionData.other_info.topic;
                qs.sub_topic=newQuestionData.other_info.sub_topic;
                docRefaz900.update("questions", FieldValue.arrayUnion(newDocRef));
                docRefaz900.update("question_short_info", FieldValue.arrayUnion(qs));







            }










            // Iterate and print to verify
/*
            for (ExamEntry entry : examEntries) {
                System.out.println("------------------------------------------------");
                System.out.println("Exam Ref: " + entry.getExam_ref());

                // Navigate deep to get the question text
                if (entry.getQuestion_data() != null &&
                        entry.getQuestion_data().getBasicInfo() != null) {

                    List<QuestionDetail> details = entry.getQuestion_data().getBasicInfo().getQuestionDetails();
                    if (details != null && !details.isEmpty()) {
                        System.out.println("Question: " + details.get(0).getText());
                    }
                }

                // Print the correct answer
                if (entry.getQuestion_data() != null && entry.getQuestion_data().getChoices() != null) {
                    System.out.print("Correct Answer: ");
                    for (Choice c : entry.getQuestion_data().getChoices()) {
                        if (c.isAnswer()) {
                            System.out.println(c.getChoice());
                        }
                    }
                }
            }

*/




        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}