package com.kalsym.locationservice.repository;

import com.kalsym.locationservice.model.Config.ProductFeatureConfig;
import com.kalsym.locationservice.model.Config.StoreConfig;

import org.springframework.data.domain.Pageable;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductFeaturedRepository extends JpaRepository<ProductFeatureConfig,Integer> {
    

    // " SELECT pwd "
    // + "FROM ProductMain pwd "
    // + "WHERE pwd.status IN :status "
    // + "AND pwd.storeCategory.parentCategoryId LIKE CONCAT('%', :parentCategoryId ,'%') "
    // + "AND pwd.name LIKE CONCAT('%', :name ,'%') "
    // + "AND pwd.storeDetails.regionCountryId = :regionCountryId "
    // + "AND pwd.status IN :status "
    // + "AND pwd.storeDetails.regionCityDetails.name LIKE CONCAT('%', :cityName ,'%') "
    // + "AND pwd.storeDetails.city LIKE CONCAT('%', :cityId ,'%')"

    @Query(
        " SELECT pfc "
        + "FROM ProductFeatureConfig pfc "
        + "WHERE productDetails.status IN :status "
        + "AND productDetails.storeCategory.parentCategoryId LIKE CONCAT('%', :parentCategoryId ,'%') "
        + "AND productDetails.name LIKE CONCAT('%', :name ,'%') "
        + "AND productDetails.storeDetails.regionCountryId = :regionCountryId "
        + "AND productDetails.status IN :status "
        + "AND productDetails.storeDetails.regionCityDetails.name LIKE CONCAT('%', :cityName ,'%') "
        + "OR productDetails.storeDetails.city IN :cityId"
    )
    Page<ProductFeatureConfig> getQueryProductConfig(
        @Param("status") List<String> status,
        @Param("regionCountryId") String regionCountryId,
        @Param("parentCategoryId") String parentCategoryId,
        @Param("cityId") List<String> cityId,
        @Param("cityName") String cityName,
        @Param("name") String name,
        Pageable pageable
    );
}


