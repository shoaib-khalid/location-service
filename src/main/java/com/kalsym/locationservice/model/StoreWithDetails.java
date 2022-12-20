package com.kalsym.locationservice.model;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonFormat;

import org.hibernate.annotations.Formula;

import com.kalsym.locationservice.model.Config.StoreFeaturedSimple;

@Entity
@Table(name = "store")
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class StoreWithDetails implements Serializable  { 
    
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
    
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date created;
    
    @OneToMany(cascade = CascadeType.ALL,
    fetch = FetchType.LAZY)
    @JoinColumn(name = "storeId", insertable = false, updatable = false, nullable = true)
    private List<Category> storeCategory;
    
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
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", referencedColumnName = "storeId", insertable = false, updatable = false, nullable = true)
    private StoreFeaturedSimple featuredStore;
        
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date snoozeStartTime;
    
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date snoozeEndTime;
    
    private String snoozeReason;

    private Boolean isDineIn;

    private String dineInOption;

    private String dineInPaymentType;

    private Boolean isDelivery;

    private Boolean isAlwaysOpen;

    @Transient 
    private Boolean isOpen;

    @Transient
    Boolean isSnooze;

    @Transient
    StoreSnooze storeSnooze;
    
    @Transient
    private Double distanceInMeter;
    
    @Formula("isStoreOpen(id)" )
    private Boolean isStoreOpen;
    
}
