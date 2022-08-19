package com.kalsym.locationservice.model.Config;


import java.io.Serializable;
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
import com.kalsym.locationservice.model.Category;
import com.kalsym.locationservice.model.RegionCity;
import com.kalsym.locationservice.model.Store;
import com.kalsym.locationservice.model.StoreCategory;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "store_display_config")
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
public class StoreFeaturedConfig implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String storeId;

    private Integer sequence;

    private Boolean isMainLevel;

    private Integer mainLevelSequence;

}
