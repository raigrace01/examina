package com.appnest.breedit.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;

import java.util.List;



public class Farm {

    @DocumentId
    public DocumentReference docID; //handle doc id

    @PropertyName("farm_name")
    public String farmName;

    public DocumentReference owner;

    public Timestamp date;

    public String description;

    public String country;

    public List<String> images;

    @PropertyName("default_image")
    public String defaultImage;


    @PropertyName("image_data")
    public List<Image> imageData;

    @PropertyName("last_update")
    public Timestamp lastUpdate;

    @PropertyName("farm_ref")
    public DocumentReference farmRef;   // for view


    @PropertyName("is_sync_main_view")
    public boolean isSyncMainView;




}
