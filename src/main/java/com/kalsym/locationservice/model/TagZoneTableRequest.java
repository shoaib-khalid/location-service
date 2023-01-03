package com.kalsym.locationservice.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter

public class TagZoneTableRequest {

    private Integer id;

    private String zoneName;
    
    private Integer tagId;

    private List<TagTableRequest> tagTable;
    
}




