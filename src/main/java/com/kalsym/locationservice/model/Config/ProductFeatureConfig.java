package com.kalsym.locationservice.model.Config;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kalsym.locationservice.model.Store;
import com.kalsym.locationservice.model.Product.ProductMain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "product_feature_config")
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
public class ProductFeatureConfig {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String productId;

    private Integer sequence;

    @OneToOne()
    @JoinColumn(name = "productId",referencedColumnName="id", insertable = false, updatable = false, nullable = true)
    private ProductMain productDetails;

}

