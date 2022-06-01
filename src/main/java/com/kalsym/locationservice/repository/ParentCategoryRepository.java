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
            +"WHERE storeLocation.state LIKE CONCAT('%', :state ,'%') "
            +"OR storeLocation.city LIKE CONCAT('%', :city ,'%') "
            +"OR storeLocation.postcode LIKE CONCAT('%', :postcode ,'%') "
            +"OR storeLocation.regionCountryId LIKE CONCAT('%', :regionCountryId ,'%') "
            +"GROUP BY pc.parentId"
    )
    Page<ParentCategory> getParentCategoriesBasedOnLocationQuery(
        @Param("state") String state,
        @Param("city") String city,
        @Param("postcode") String postcode,
        @Param("regionCountryId") String regionCountryId,
        Pageable pageable
    );

}

