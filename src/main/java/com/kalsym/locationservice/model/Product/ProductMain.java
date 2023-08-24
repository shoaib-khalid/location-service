package com.kalsym.locationservice.model.Product;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kalsym.locationservice.LocationServiceApplication;
import com.kalsym.locationservice.model.Category;
import com.kalsym.locationservice.model.Config.ProductFeatureSimple;
import com.kalsym.locationservice.model.Store;
import javax.persistence.ManyToOne;

import com.kalsym.locationservice.model.Voucher;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "product")
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
// , Comparable< ProductMain >
public class ProductMain implements Serializable  {
    
    @Id
    private String id;

    private String name;

    private String thumbnailUrl;

    private String seoUrl;

    private String seoName;

    private String status;

    private Boolean allowOutOfStockPurchases;

    private Boolean isCustomPrice;

    private String voucherId;

    @OneToOne()
    @JoinColumn(name = "storeId",referencedColumnName="id")
    private Store storeDetails;

    @OneToOne()
    @JoinColumn(name = "voucherId",referencedColumnName="id", updatable = false, insertable = false)
    private Voucher voucherDetails;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date created;

    // @OneToMany(cascade = CascadeType.ALL,
    //         fetch = FetchType.LAZY)
    // @JoinColumn(name = "id", referencedColumnName = "productId")
    // private List<ProductInventoryWithDetails> productInventories;

    @OneToMany(cascade = CascadeType.ALL,
    fetch = FetchType.LAZY)
    @JoinColumn(name = "productId")
    private List<ProductInventoryWithDetails> productInventories;

    @OneToOne()
    @JoinColumn(name = "categoryId",referencedColumnName="id", insertable = false, updatable = false, nullable = true)
    private Category storeCategory;  

    private Integer shortId;

    @Transient 
    String seoUrlMarketPlace;

    @Transient 
    String seoUrlSf;

    @Transient 
    String seoNameMarketplace;

    public String getThumbnailUrl() {
        if (thumbnailUrl==null)
            return null;
        else
            return LocationServiceApplication.ASSETURL+ thumbnailUrl;
    }

    public String getSeoUrlMarketPlace() {

        return LocationServiceApplication.MARKETPLACEURL+"/"+shortId+"-"+seoName;
    }

    public String getSeoUrlSf() {

        return seoUrl+"/"+shortId;
    }

    public String getSeoNameMarketplace() {

        return shortId+"-"+seoName;
    }
    
    //  @Override
    // public int compareTo(ProductMain o) {
    //     return this.getStoreDetails().getDistanceInMeter().compareTo(o.getStoreDetails().getDistanceInMeter());
    // }
        
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", referencedColumnName = "productId", insertable = false, updatable = false, nullable = true)
    private ProductFeatureSimple featuredProduct;


}
