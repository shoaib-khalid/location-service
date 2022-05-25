package com.kalsym.locationservice.repository;

import java.util.List;

import com.kalsym.locationservice.model.Category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;



@Repository
public interface CategoryRepository extends JpaRepository<Category,String> {
    
    //Note that if we want to use Raw Query u need to use Model Name instad of table name
    @Query(
        value = 
        " SELECT "
        +"sc.id AS parentCatId, "
        +"sc.name as parentName, "
        +"sc.thumbnailUrl as parentThumnailUrl, "
        +"category.name as categoryName, "
        +"category.thumbnailUrl  as catThumnail, "
        +"category.storeId  as catStoreId, "
        +"category.id as categoryId, "
        +"storeLocation.* "
        +"FROM store_category sc "
        +"INNER JOIN store_category AS category ON category.parentCategoryId  = sc.id "
        +"INNER JOIN store AS storeLocation on storeLocation.id = category.storeId "
        +"WHERE storeLocation.regionCountryStateId LIKE CONCAT('%', :state ,'%') "
        +"OR storeLocation.city LIKE CONCAT('%', :city ,'%') "
        +"OR storeLocation.postcode LIKE CONCAT('%', :postcode ,'%') "
        +"OR storeLocation.regionCountryId LIKE CONCAT('%', :regionCountryId ,'%') "
        +"group by parentCatId",
        nativeQuery = true)
    List<Object[]> getParentCategoriesBasedOnLocation(
        @Param("state") String state,
        @Param("city") String city,
        @Param("postcode") String postcode,
        @Param("regionCountryId") String regionCountryId
        );

}

