package com.kalsym.locationservice.repository;

import java.util.List;

import com.kalsym.locationservice.model.Product.ProductMain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

@Repository
public interface ProductRepository extends JpaRepository<ProductMain,String> {
    
    @Query(
        " SELECT pwd "
        + "FROM ProductMain pwd "
        + "WHERE pwd.status IN :status "
        + "AND pwd.storeCategory.parentCategoryId LIKE CONCAT('%', :parentCategoryId ,'%') "
        + "AND pwd.name LIKE CONCAT('%', :name ,'%') "
        + "AND pwd.storeDetails.regionCountryId = :regionCountryId "
        + "AND pwd.status IN :status "
        + "AND pwd.storeDetails.regionCityDetails.name LIKE CONCAT('%', :cityName ,'%') "
        + "AND pwd.storeDetails.city IN :cityId"
    )
    Page<ProductMain> getProductByParentCategoryIdAndLocation(
            @Param("status") List<String> status,
            @Param("regionCountryId") String regionCountryId,
            @Param("parentCategoryId") String parentCategoryId,
            @Param("cityId") List<String> cityId,
            @Param("cityName") String cityName,
            @Param("name") String name,
            Pageable pageable
    );
    

}

