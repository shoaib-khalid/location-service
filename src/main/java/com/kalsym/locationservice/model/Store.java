package com.kalsym.locationservice.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;



@Entity
@Table(name = "store")
@Getter
@Setter
@JsonInclude(JsonInclude.Include.ALWAYS)
@ToString
@NoArgsConstructor
public class Store implements Serializable {

    @Id
    private String id;

    private String name;

    private String city;

    @OneToOne()
    @JoinColumn(name = "city",referencedColumnName="id", insertable = false, updatable = false, nullable = true)
    private RegionCity regionCityDetails; 

    private String storeDescription;

    private String domain;
    
    @Column(name="regionCountryStateId")
    private String state;

    private String postcode;

    private String regionCountryId;
     
    private String longitude;
    
    private String latitude;
    
    private String verticalCode;
    
    // @OneToOne()
    // @JoinColumn(name = "id",referencedColumnName="storeId", insertable = false, updatable = false, nullable = true)
    // private StoreAsset storeAsset;
    
    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @JoinColumn(name = "storeId", insertable = false, updatable = false, nullable = true)
    private List<StoreAssets> storeAssets;

    @OneToMany(cascade = CascadeType.ALL,
    fetch = FetchType.LAZY)
    @JoinColumn(name = "storeId", insertable = false, updatable = false, nullable = true)
    private List<TagStoreDetails> storeTag;

    @OneToMany(cascade = CascadeType.ALL,
    fetch = FetchType.EAGER)
    @JoinColumn(name = "storeId", insertable = false, updatable = false, nullable = true)
    private List<StoreTiming> storeTiming;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date snoozeStartTime;
    
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date snoozeEndTime;
    
    private String snoozeReason;

    // private Boolean isDisplayMap;

    private Boolean isDineIn;

    private String dineInOption;

    private String dineInPaymentType;

    private Boolean isDelivery;

    @Transient
    Boolean isSnooze;

    @Transient
    StoreSnooze storeSnooze;
    
    @Transient
    private Double distanceInMeter;



}
