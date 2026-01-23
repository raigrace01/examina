package com.appnest.breedit.model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.annotation.DocumentId;
//import com.google.cloud.firestore.annotation.PropertyName;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.cloud.firestore.annotation.PropertyName;

import java.util.List;



public class Animal {
    @DocumentId
    public DocumentReference docID;
    public DocumentReference owner;
    public String breed;
    public String color;
    public String status;
    public String id;
    public String gender;
    public String cage_identifier;
    public List<Notes> notes;
    public String friendly_name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<String> images;
    public Timestamp date_added;
    public List<DocumentReference> mark_fave_user;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<Bloodline> bloodline;
    public String default_image;
    public String filial;
    public List<Weight> weight;
    public Double weight_latest;
    public List<String> tags;
    public Farm farm_data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Image image_data;
    public Timestamp last_update;
    public Timestamp last_access_date;
    public String category;
    public Timestamp date_of_birth;
    public Animal mother_data;
    public Animal father_data;
    public List<Animal> children;
    public DocumentReference marketplace_ref;
    public DocumentReference ref; // for view only
    @PropertyName("is_sync_main_view")
    public boolean isSyncMainView;



}
