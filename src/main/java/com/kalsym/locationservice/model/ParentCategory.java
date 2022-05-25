package com.kalsym.locationservice.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import javax.persistence.Column;

@Entity
@Table(name = "store_category")
@Getter
@Setter
@JsonInclude(JsonInclude.Include.ALWAYS)
@ToString
@NoArgsConstructor
public class ParentCategory implements Serializable {
    
    @Id
    @Column(name="id")
    private String parentId;

    @Column(name="name")
    private String parentName;

    @Column(name="thumbnailUrl")
    private String parentThumbnailUrl;


}
