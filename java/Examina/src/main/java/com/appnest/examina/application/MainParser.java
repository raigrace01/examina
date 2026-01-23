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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class MainParser {
    private static final Logger logger = LogManager.getLogger(MainParser.class);
    private static Firestore db;
    private String QUESTION_STATUS = "published";
    private static DocumentReference docRefExam;

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        MainParser parser = new MainParser();
        FileInputStream serviceAccount = new FileInputStream("D:/ryan/examinaServiceAccountKey.json");
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setProjectId("examina-vy0zd6")
                .build();
        FirebaseApp.initializeApp(options);
        db = FirestoreClient.getFirestore();

        List<QueryDocumentSnapshot> documents = db.collection("exam").get().get().getDocuments();
        for(QueryDocumentSnapshot doc:documents){
            if(doc.get("exam_code").equals("AZ-900")){
                docRefExam = doc.getReference();
            }
        }




        DocumentSnapshot docExam = docRefExam.get().get();
        DocumentReference docRefQuestionAssoc = (DocumentReference) docExam.get("question_list_assoc_ref");
        DocumentSnapshot docQuestionAssoc = docRefQuestionAssoc.get().get();
        ExamQuestionAssoc assoc = docQuestionAssoc.toObject(ExamQuestionAssoc.class);

        List<QuestionShortInfo> qsList = new ArrayList<>();

        //Entire path
        Path basedir = Paths.get("inputs\\az900");
        Files.list(basedir).forEach(path -> {
                    parser.insertEntries(path);
                }
        );
        Stream<Path> stream= Files.list(basedir);
        List<Path> fileList;
        fileList = stream.collect(Collectors.toList());
        for (Path path : fileList) {
            List<QuestionShortInfo> alist = parser.insertEntries(path);
            for(QuestionShortInfo qs:alist){
                qsList.add(qs);
            }

       }
        for(QuestionShortInfo qs: qsList){
            //docRefQuestionAssoc.update("data", FieldValue.arrayUnion(qs));
            if(assoc.data == null){
                assoc.data = new ArrayList<QuestionShortInfo>();
            }
            assoc.data.add(qs);
        }
        docRefQuestionAssoc.update("data", assoc.data);

        //Individual path
        //parser.insertEntries(Paths.get("inputs\\az900\\batch_1.txt"));








    }



    public List<QuestionShortInfo> insertEntries(Path path){
        List<QuestionShortInfo> qsList = new ArrayList<>();
        logger.info("log level {}",logger.getLevel());
        logger.info("Starting application...");
        ObjectMapper mapper = new ObjectMapper();
        // Prevents crashing if the JSON contains fields not in your Java classes
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            // Ensure this matches your actual file name
            //Path path = Paths.get(filename);
            //File file = new File("D:\\ryan\\examina\\az900\\"+filename);
            File file = path.toFile();

            // Parse the JSON array into a List of ExamEntry objects
            List<ExamEntry> examEntries = mapper.readValue(file, new TypeReference<List<ExamEntry>>(){});

            logger.info("Connected to firebase db {}",db.getOptions().getDatabaseId());







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
                question.exam_ref=docRefExam;


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
                //docRefaz900.update("questions", FieldValue.arrayUnion(newDocRef));
                //docRefaz900.update("question_short_info", FieldValue.arrayUnion(qs));

                // try to add question info on existing exam_question_assoc doc
                qsList.add(qs);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return qsList;
    }
}