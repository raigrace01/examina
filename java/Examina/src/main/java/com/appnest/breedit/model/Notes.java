package com.appnest.breedit.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.cloud.Timestamp;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class Notes {
    public String note;
    public Timestamp date;
}
