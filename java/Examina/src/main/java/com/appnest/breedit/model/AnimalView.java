package com.appnest.breedit.model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.PropertyName;

import java.util.List;

public class AnimalView {

    @PropertyName("animal_data")
    public List<Animal> animalData;
    @PropertyName("last_update")
    public Timestamp lastUpdate;

}
