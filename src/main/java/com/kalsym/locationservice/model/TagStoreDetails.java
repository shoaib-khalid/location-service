package com.kalsym.locationservice.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "tag_details")
@Getter
@Setter
@JsonInclude(JsonInclude.Include.ALWAYS)
@ToString
@NoArgsConstructor
public class TagStoreDetails {

    @JsonIgnore
    @Id
    private Integer id;

    private Integer tagId;
    // @Id
    private String storeId;

    private Boolean isFoodCourtOwner;

    // private String categoryId;

    @OneToOne()
    @JoinColumn(name = "tagId",referencedColumnName="id", insertable = false, updatable = false, nullable = true)
    private TagKeyword tagKeyword; 

    // @OneToOne()
    // @JoinColumn(name = "storeId",referencedColumnName="id", insertable = false, updatable = false, nullable = true)
    // private Store storeDetails; 

}
