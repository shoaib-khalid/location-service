package com.kalsym.locationservice.model;


import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kalsym.locationservice.model.Config.TagConfig;
import java.io.Serializable;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "tag_table")
@Getter
@Setter
@JsonInclude(JsonInclude.Include.ALWAYS)
@ToString
@NoArgsConstructor
public class TagTable implements Serializable {

    @Id
    private Integer id;

    private Integer zoneId;

    private String tablePrefix;
    
    private Integer tableNoStart;
    
    private Integer tableNoEnd;
    
    @Transient
    private List<String> tableNoList;

    public static TagTable castReference(TagTableRequest req){

        TagTable body = new TagTable();
        //set the id for update data
        if(req.getId() != null){

            body.setId(req.getId());

        }
        body.setZoneId(req.getZoneId());
        body.setTablePrefix(req.getTablePrefix());
        body.setTableNoStart(req.getTableNoStart());
        body.setTableNoEnd(req.getTableNoEnd());

        return body;
    }

    public static TagTable updateData(TagTable data,TagTable newBody){

        data.setZoneId(newBody.getZoneId());
        data.setTablePrefix(newBody.getTablePrefix());
        data.setTableNoStart(newBody.getTableNoStart());
        data.setTableNoEnd(newBody.getTableNoEnd());
        return data;

    }
}
