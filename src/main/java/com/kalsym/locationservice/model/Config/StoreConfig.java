package com.kalsym.locationservice.model.Config;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kalsym.locationservice.model.RegionCity;
import com.kalsym.locationservice.model.Store;

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
public class StoreConfig {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Boolean isDisplay;

    private String storeId;

    private Integer sequence;

    @OneToOne()
    @JoinColumn(name = "storeId",referencedColumnName="id", insertable = false, updatable = false, nullable = true)
    private Store storeDetails; 
}
