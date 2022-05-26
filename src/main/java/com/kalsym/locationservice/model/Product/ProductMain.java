package com.kalsym.locationservice.model.Product;

import java.io.Serializable;
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
import com.kalsym.locationservice.model.Store;

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

public class ProductMain implements Serializable {
    
    @Id
    private String id;

    private String name;

    private String thumbnailUrl;
    
    private String status;

    @OneToOne()
    @JoinColumn(name = "storeId",referencedColumnName="id")
    private Store storeDetails; 

    // @OneToMany(cascade = CascadeType.ALL,
    //         fetch = FetchType.LAZY)
    // @JoinColumn(name = "id", referencedColumnName = "productId")
    // private List<ProductInventoryWithDetails> productInventories;

    @OneToMany(cascade = CascadeType.ALL,
    fetch = FetchType.LAZY)
    @JoinColumn(name = "productId")
    private List<ProductInventoryWithDetails> productInventories;
}
