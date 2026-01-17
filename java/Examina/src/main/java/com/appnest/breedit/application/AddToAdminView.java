package com.appnest.breedit.application;

import com.appnest.breedit.model.AnimalView;
import com.appnest.breedit.model.Farm;
import com.appnest.breedit.model.FarmView;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.appnest.breedit.model.Animal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;



public class AddToAdminView {

    // target number of animals
    private static final int numRowAnimal = 5;

    // target number of farm
    private static final int numRowFarm = 5;// number of farm to pull in db

    // number of animal to be replaced
    private static final int defaultAnimalReplaceCount = 2;

    // number of animal to be replaced
    private static final int defaultFarmReplaceCount = 2;

    private static final Logger logger = LogManager.getLogger(AddToAdminView.class);

    public static void main(String[] args) throws IOException {

        logger.info("log level {}",logger.getLevel());
        logger.info("Starting application...");

        FileInputStream serviceAccount = new FileInputStream("E:/ryan/serviceAccountKey.json");
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://breedit-dev.firebaseio.com")
                .build();
        FirebaseApp.initializeApp(options);
        Firestore db = FirestoreClient.getFirestore();
        logger.info("Connected to firebase db {}",db.getOptions().getDatabaseId());


        ScheduledExecutorService executor =
                Executors.newScheduledThreadPool(4);
        Runnable animalViewTask = new Runnable() {
            public void run() {
                // Invoke method(s) to do the work
                logger.info("Executing task for animal view");
                executeAnimalView(db);
            }
        };
        Runnable farmViewTask = new Runnable() {
            public void run() {
                // Invoke method(s) to do the work
                logger.info("Executing task for farm view");
                executeFarmView(db);
            }
        };

        executor.scheduleAtFixedRate(animalViewTask, 0, 15, TimeUnit.MINUTES);
        executor.scheduleAtFixedRate(farmViewTask, 0, 15, TimeUnit.MINUTES);


    }

    //create basic animalInfo
    private static void basicAnimalView(Firestore db){
        try {
            logger.info("Setting up basic animal collection as it is empty");
            CollectionReference animals = db.collection("animal");
            ApiFuture<QuerySnapshot> query = animals.whereEqualTo("is_sync_main_view", false).limit(numRowAnimal)
                    .orderBy("last_update", Query.Direction.ASCENDING).get();
            QuerySnapshot querySnapshot = query.get();

            List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
            List<Animal> animalList = new ArrayList<>();
            AnimalView animalView = new AnimalView();
            //logger.debug("Found  animal documents in db \n {}",documents);
            logger.info("Number of animals found in db: {}",documents.size());
            for (QueryDocumentSnapshot document : documents) {
                logger.info("Adding animal doc \n {}",document);
                Animal animal = document.toObject(Animal.class);
                logger.info("Adding animal document reference id {}",animal.docID);
                animal.ref=document.getReference();
                animalList.add(animal);
                updateDocSyncViewField(animal.ref);
            }
            animalView.animalData = animalList;
            animalView.lastUpdate = Timestamp.of(new Date());

            if(!documents.isEmpty()){
                ApiFuture<WriteResult> future = db.collection("animalViewList").document("latest").set(animalView);
                logger.info("DB Update time : {}", future.get().getUpdateTime());

            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private static void executeAnimalView(Firestore db){
        try {

            // check latest
            DocumentReference docRefAnimalViewList = db.collection("animalViewList").document("latest");
            ApiFuture<DocumentSnapshot> futureAnimalViewList = docRefAnimalViewList.get();
            DocumentSnapshot documentAnimalViewList = futureAnimalViewList.get();
            AnimalView animalView = new AnimalView();
            animalView = documentAnimalViewList.toObject(AnimalView.class);

            if(animalView==null){
                logger.debug("AnimalView is null.... creating it");
                basicAnimalView(db);
                //executeAnimalView(db);
            }
            List<Animal> animalList;
            animalList = animalView.animalData;
            int sizeExisting = Objects.requireNonNull(animalView).animalData.size();
            int additionalIndex = 5;
            boolean isReplace = true;
            if(sizeExisting<numRowAnimal){
                additionalIndex = numRowAnimal-sizeExisting;
                isReplace=false;
            }
            // logic to maintain exact count of animals in the animal_data
            else if(sizeExisting>numRowAnimal){
                logger.info("db is inconsistent with maximun number of animals.. removing it");
                animalList.removeLast();
                return;
            }
            else{
                additionalIndex=defaultAnimalReplaceCount; //default to replace

            }

            logger.info("additional index: {}",additionalIndex);
            CollectionReference animals = db.collection("animal");
            ApiFuture<QuerySnapshot> query = animals.whereEqualTo("is_sync_main_view", false).limit(additionalIndex)
                    .orderBy("last_update", Query.Direction.ASCENDING).get();
            QuerySnapshot querySnapshot = query.get();

            List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
            logger.info("Number of animals found in db: {}",documents.size());
            logger.debug("Found  animal documents in db \n {}",documents);

            for (QueryDocumentSnapshot document : documents) {
                Animal animal = document.toObject(Animal.class);
                logger.info("Adding animal document reference id {}",animal.docID);
                animal.ref=document.getReference();
                animalList.add(animal);
                updateDocSyncViewField(animal.ref);
                if(isReplace){
                    logger.info("Removing last index in animal_data");
                    animalList.removeLast();
                }

            }
            animalView.animalData = animalList;
            animalView.lastUpdate = Timestamp.of(new Date());

            if(!documents.isEmpty()){
                ApiFuture<WriteResult> future = db.collection("animalViewList").document("latest").set(animalView);
                logger.info("DB Update time : {}", future.get().getUpdateTime());
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    //create basic animalInfo
    private static void basicFarmView(Firestore db){
        try {
            logger.info("Setting up basic farm collection as it is empty");

            CollectionReference farms = db.collection("farm");
            ApiFuture<QuerySnapshot> query = farms.whereEqualTo("is_sync_main_view", false).limit(5)
                    .orderBy("last_update", Query.Direction.ASCENDING).get();
            QuerySnapshot querySnapshot = query.get();

            List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
            List<Farm> farmList = new ArrayList<>();
            FarmView farmView = new FarmView();
            logger.info("Number of farm found in db: {}",documents.size());
            for (QueryDocumentSnapshot document : documents) {
                logger.info("Adding farm doc \n {}",document);
                Farm farm = document.toObject(Farm.class);
                logger.info("Adding farm document reference id {}",farm.docID);
                farm.farmRef=document.getReference();
                farmList.add(farm);
                updateDocSyncViewField(farm.docID);
            }
            farmView.farmData = farmList;
            farmView.lastUpdate = Timestamp.of(new Date());
            if(!documents.isEmpty()){
                ApiFuture<WriteResult> future = db.collection("farmViewList").document("latest").set(farmView);
                logger.info("DB Update time : {}", future.get().getUpdateTime());
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
    private static void executeFarmView(Firestore db){
        try {

            // check latest
            DocumentReference docRefFarmViewList = db.collection("farmViewList").document("latest");
            ApiFuture<DocumentSnapshot> futureAnimalViewList = docRefFarmViewList.get();
            DocumentSnapshot documentAnimalViewList = futureAnimalViewList.get();
            FarmView farmView = new FarmView();
            farmView = documentAnimalViewList.toObject(FarmView.class);
            List<Farm> farmList;

            if(farmView==null){
                logger.debug("FarmView is null.... creating it");
                basicFarmView(db);
                //executeFarmView(db);
            }
            farmList = Objects.requireNonNull(farmView).farmData;
            int sizeExisting = Objects.requireNonNull(farmView).farmData.size();
            int additionalIndex = 5;
            boolean isReplace = true;

            if(sizeExisting<numRowFarm){
                additionalIndex = numRowFarm-sizeExisting;
                isReplace=false;
            }
            else if(sizeExisting>numRowFarm){
                farmList.removeLast(); // this is to make sure to maintain the target number of rows
                return;
            }
            else{
                additionalIndex=defaultFarmReplaceCount; //default to replace
            }

            logger.info("additional index: {}",additionalIndex);
            CollectionReference animals = db.collection("farm");
            ApiFuture<QuerySnapshot> query = animals.whereEqualTo("is_sync_main_view", false).limit(additionalIndex)
                    .orderBy("last_update", Query.Direction.ASCENDING).get();
            QuerySnapshot querySnapshot = query.get();
            List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
            logger.info("Number of farm found in db: {}",documents.size());
            logger.debug("Found farm documents in db \n {}",documents);
            for (QueryDocumentSnapshot document : documents) {
                Farm farm = document.toObject(Farm.class);
                logger.info("Adding farm document reference id {}",farm.docID);
                farm.farmRef=document.getReference();
                farmList.add(farm);
                updateDocSyncViewField(farm.docID);
                if(isReplace){
                    farmList.removeLast();
                }
            }

            farmView.farmData = farmList;
            farmView.lastUpdate = Timestamp.of(new Date());

            if(!documents.isEmpty()){
                ApiFuture<WriteResult> future = db.collection("farmViewList").document("latest").set(farmView);
                logger.info("Update time : {}", future.get().getUpdateTime());
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
    private static void updateDocSyncViewField(DocumentReference docRef){
        try {
            logger.info("Updating is_sync_main_view of document {}",docRef);
            ApiFuture<WriteResult> future= docRef.update("is_sync_main_view",true);
            logger.info("DB Update time for animal is_sync_main_view: {}", future.get().getUpdateTime());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }




}