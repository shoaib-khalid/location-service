package com.kalsym.locationservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@JsonInclude(Include.NON_NULL)

public class TagTableRequest {
    
    private Integer id;

    private Integer zoneId;

    private String tablePrefix;

    private String tableNumber;

    private String combinationTableNumber;

}
