package com.kalsym.locationservice.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kalsym.locationservice.LocationServiceApplication;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "store_category")
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class StoreCategory implements Serializable {
    
    @Id
    private String id;

    private String name;

    private String thumbnailUrl;

    private String parentCategoryId;

    private String storeId;

    @OneToOne()
    @JoinColumn(name = "parentCategoryId",referencedColumnName="id", insertable = false, updatable = false, nullable = true)
    private ParentCategory parentCategory;   

    @OneToOne()
    @JoinColumn(name = "storeId",referencedColumnName="id", insertable = false, updatable = false, nullable = true)
    private Store storeDetails; 

    public String getThumbnailUrl() {
        if (thumbnailUrl==null)
            return null;
        else
            return LocationServiceApplication.ASSETURL+ thumbnailUrl;
    }
}
