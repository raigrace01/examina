package com.appnest.breedit.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class User {

    public Timestamp created_time;
    public String display_name;
    public String email;
    public DocumentReference farm;
    public String photo_url;
    public String uid;


}
