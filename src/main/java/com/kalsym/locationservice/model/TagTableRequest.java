package com.kalsym.locationservice.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter

public class TagTableRequest {
    
    private Integer id;

    private Integer zoneId;

    private String tablePrefix;

    private Integer tableNoStart;

    private Integer tableNoEnd;

}
