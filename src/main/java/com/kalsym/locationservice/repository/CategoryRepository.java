package com.kalsym.locationservice.repository;

import java.util.List;

import com.kalsym.locationservice.model.Category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;



@Repository
public interface CategoryRepository extends JpaRepository<Category,String> {
    
    //Note that if we want to use Raw Query , need to specify nativeQuery = true, 
    //if you dont specify then make sure the table name u use model name.
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
        +"AND sc.id = :parentCategoryId "
        +"group by parentCatId",
        nativeQuery = true)
    List<Object[]> getRawStoreBasedOnParentCategories(
        @Param("state") String state,
        @Param("city") String city,
        @Param("postcode") String postcode,
        @Param("regionCountryId") String regionCountryId,
        @Param("parentCategoryId") String parentCategoryId
        );

    @Query(
            " SELECT sc "
            + "FROM Category sc "
            + "WHERE sc.storeDetails.regionCountryId = :regionCountryId "
            + "AND sc.parentCategory.parentId LIKE CONCAT('%', :parentCategoryId ,'%') "
            + "AND sc.storeDetails.name LIKE CONCAT('%', :storeName ,'%') "
            + "AND sc.storeDetails.regionCityDetails.name LIKE CONCAT('%', :cityName ,'%') "
            + "AND sc.storeDetails.city LIKE CONCAT('%', :city ,'%') "
            + "OR sc.storeDetails.state = :state "
            + "OR sc.storeDetails.postcode = :postcode "
            + "GROUP BY sc.storeDetails.id"
    )
    Page<Category> getStoreBasedOnParentCategories(
        @Param("city") String city,
        @Param("cityName") String cityName,
        @Param("state") String state,
        @Param("regionCountryId") String regionCountryId,
        @Param("postcode") String postcode,
        @Param("parentCategoryId") String parentCategoryId,
        @Param("storeName") String storeName,
        Pageable pageable
    );

}

