package com.kalsym.locationservice.repository;


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
public interface StoreConfigRepository extends JpaRepository<StoreConfig,Integer>, JpaSpecificationExecutor<StoreConfig> {
    
    @Query(
        " SELECT sdc "
        + "FROM StoreConfig sdc "
        + "INNER JOIN Store s on sdc.storeId = s.id "
        + "INNER JOIN Category sc on sc.storeId = sdc.storeId "
        + "WHERE s.regionCountryId LIKE CONCAT('%', :regionCountryId ,'%') "
        + "AND sc.parentCategoryId LIKE CONCAT('%', :parentCategoryId ,'%') "
        + "OR s.city IN :cityId "
        + "AND s.regionCityDetails.name LIKE CONCAT('%', :cityName ,'%') "
        + "GROUP BY sdc.id"
    )
    Page<StoreConfig> getQueryStoreConfigRaw(
        @Param("cityId") List<String> cityId,
        @Param("cityName") String cityName,
        @Param("regionCountryId") String regionCountryId,
        @Param("parentCategoryId") String parentCategoryId,
        Pageable pageable
    );
}


