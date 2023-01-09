package com.kalsym.locationservice.model;


import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kalsym.locationservice.model.Config.TagConfig;
import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "tag_zone")
@Getter
@Setter
@JsonInclude(JsonInclude.Include.ALWAYS)
@ToString
@NoArgsConstructor
public class TagZone implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer tagId;

    private String zoneName;
   
    @OneToMany(cascade = CascadeType.ALL,
    fetch = FetchType.LAZY)
    @JoinColumn(name = "zoneId", insertable = false, updatable = false, nullable = true)    
    private List<TagTable> tagTables;

    public static TagZone castReference(TagZoneTableRequest req){

        TagZone body = new TagZone();
        //set the id for update data
        if(req.getId() != null){

            body.setId(req.getId());

        }
        body.setTagId(req.getTagId());
        body.setZoneName(req.getZoneName());

        return body;
    }

    public static TagZone updateData(TagZone data,TagZone newBody){

        data.setTagId(newBody.getTagId());
        data.setZoneName(newBody.getZoneName());



        return data;

    }
}
