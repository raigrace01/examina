package com.appnest.breedit.model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;

import java.util.List;


public class FarmView {

    @PropertyName("farm_data")
    public List<Farm> farmData;
    @PropertyName("last_update")
    public Timestamp lastUpdate;




}
