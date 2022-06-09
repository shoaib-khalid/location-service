package com.kalsym.locationservice.repository;

import java.util.List;

import com.kalsym.locationservice.model.Category;
import com.kalsym.locationservice.model.ParentCategory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;



@Repository
public interface ParentCategoryRepository extends JpaRepository<ParentCategory,String> {
    
    @Query(
        " SELECT pc "
        +"FROM ParentCategory pc "
        +"INNER JOIN Category category ON category.parentCategoryId  = pc.id "
        +"INNER JOIN Store storeLocation on storeLocation.id = category.storeId "
        +"WHERE storeLocation.regionCountryId LIKE CONCAT('%', :regionCountryId ,'%') "
        +"AND pc.parentId LIKE CONCAT('%', :parentCategoryId ,'%') "
        +"AND storeLocation.city IN :city "
        +"OR storeLocation.state LIKE CONCAT('%', :state ,'%') "
        +"OR storeLocation.postcode LIKE CONCAT('%', :postcode ,'%') "
        +"GROUP BY pc.parentId"
    )
    Page<ParentCategory> getParentCategoriesBasedOnLocationWithCityIdQuery(
        @Param("state") String state,
        @Param("city") List<String> city,
        @Param("postcode") String postcode,
        @Param("regionCountryId") String regionCountryId,
        @Param("parentCategoryId") String parentCategoryId,
        Pageable pageable
    );

    @Query(
        " SELECT pc "
        +"FROM ParentCategory pc "
        +"WHERE pc.verticalCode IN :verticalCode "
        +"AND pc.parentId LIKE CONCAT('%', :parentCategoryId ,'%')"
    )
    Page<ParentCategory> getAllParentCategoriesBasedOnCountry(
        @Param("verticalCode") List<String> verticalCode,
        @Param("parentCategoryId") String parentCategoryId,
        Pageable pageable
    );

    

}

