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

public class Category implements Serializable {
    
    @Id
    private String id;

    private String name;

    private String thumbnailUrl;

    @OneToOne()
    @JoinColumn(name = "parentCategoryId",referencedColumnName="id")
    private ParentCategory parentCategory;   

    @OneToOne()
    @JoinColumn(name = "storeId",referencedColumnName="id")
    private Store storeDetails; 
}
