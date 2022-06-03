package com.kalsym.locationservice.model.Config;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name="created_at")
    private Date createdAt;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name="updated_at")
    private Date updatedAt;

}

