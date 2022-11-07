package com.kalsym.locationservice.repository;

import java.util.List;

import com.kalsym.locationservice.model.Product.ProductMain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

@Repository
public interface ProductRepository extends JpaRepository<ProductMain,String>, PagingAndSortingRepository<ProductMain, String>, JpaSpecificationExecutor<ProductMain> {
    
    @Query(
        " SELECT pwd "
        + "FROM ProductMain pwd "
        + "WHERE pwd.status IN :status "
        + "AND pwd.storeCategory.parentCategoryId LIKE CONCAT('%', :parentCategoryId ,'%') "
        + "AND pwd.name LIKE CONCAT('%', :name ,'%') "
        + "AND pwd.storeDetails.regionCountryId = :regionCountryId "
        + "AND pwd.status IN :status "
        + "AND pwd.storeDetails.regionCityDetails.name LIKE CONCAT('%', :cityName ,'%') "
        + "ORDER BY pwd.thumbnailUrl NULLS LAST"

        
    )
    Page<ProductMain> getProductByParentCategoryIdAndLocation(
            @Param("status") List<String> status,
            @Param("regionCountryId") String regionCountryId,
            @Param("parentCategoryId") String parentCategoryId,
            @Param("cityName") String cityName,
            @Param("name") String name,            
            Pageable pageable
    );
    
    
     @Query(
        value =  "SELECT p.id, p.name, p.thumbnailUrl, p.seoUrl, p.seoName, p.status, "
                + "p.categoryId, p.storeId, "
                + "s.id, s.city, ST_DISTANCE_SPHERE(" +
        "      POINT(s.longitude, s.latitude),     " +
        "      POINT(:longitude, :latitude) " +
        "      ) AS distance "
        + "FROM product p "
                + "INNER JOIN store s ON p.storeId=s.id "
                + "INNER JOIN store_category c ON p.categoryId=c.id "
                + "INNER JOIN region_city r ON s.city=r.id "
        + "WHERE p.status IN (:statusList) "
        + "AND c.parentCategoryId LIKE %:parentCategoryId% "
        + "AND p.name LIKE %:productName% "
        + "AND s.regionCountryId = :regionCountryId "       
        + "AND r.name LIKE %:cityName% "
        + "ORDER BY distance",  nativeQuery = true
    )
    Page<ProductMain> getProductByParentCategoryIdAndDistance(
            @Param("statusList") List<String> statusList,
            @Param("regionCountryId") String regionCountryId,
            @Param("parentCategoryId") String parentCategoryId,
            @Param("cityName") String cityName,
            @Param("productName") String productName,
            @Param("latitude") String latitude,
            @Param("longitude") String longitude,
            Pageable pageable
    );
    
    
    /* @Query(
        value =  "SELECT p.id, p.name, p.thumbnailUrl, p.seoUrl, p.seoName, p.status, s.id, s.city, ST_DISTANCE_SPHERE(" +
        "      POINT(s.longitude, s.latitude),     " +
        "      POINT(:longitude, :latitude) " +
        "      ) AS distance "
        + "FROM ProductMain p "
        + "WHERE p.status IN (:statusList) "
        + "AND p.storeCategory.parentCategoryId LIKE %:parentCategoryId% "
        + "AND p.name LIKE %:productName% "
        + "AND p.storeDetails.regionCountryId = :regionCountryId "       
        + "AND p.storeDetails.regionCityDetails.name LIKE %:cityName% "
        + "ORDER BY distance",  nativeQuery = true
    )
    Page<ProductMain> getProductByParentCategoryIdAndDistance(
            @Param("statusList") List<String> statusList,
            @Param("regionCountryId") String regionCountryId,
            @Param("parentCategoryId") String parentCategoryId,
            @Param("cityName") String cityName,
            @Param("productName") String productName,
            @Param("latitude") String latitude,
            @Param("longitude") String longitude,
            Pageable pageable
    );*/
    

    @Query(
        " SELECT pwd "
        + "FROM ProductMain pwd "
        + "WHERE pwd.status IN :status "
        + "AND pwd.storeCategory.parentCategoryId LIKE CONCAT('%', :parentCategoryId ,'%') "
        + "AND pwd.name LIKE CONCAT('%', :name ,'%') "
        + "AND pwd.storeDetails.regionCountryId = :regionCountryId "
        + "AND pwd.status IN :status "
        + "AND pwd.storeDetails.regionCityDetails.name LIKE CONCAT('%', :cityName ,'%') "
        + "AND pwd.storeDetails.city IN :cityId "
        + "ORDER BY pwd.thumbnailUrl NULLS LAST"

    )
    Page<ProductMain> getProductByParentCategoryIdAndLocationWithCityId(
            @Param("status") List<String> status,
            @Param("regionCountryId") String regionCountryId,
            @Param("parentCategoryId") String parentCategoryId,
            @Param("cityId") List<String> cityId,
            @Param("cityName") String cityName,
            @Param("name") String name,
            Pageable pageable
    );

    @Query(
        " SELECT pwd "
        + "FROM ProductMain pwd "
        + "WHERE pwd.seoName IN :seoName "
        + "AND pwd.storeDetails.regionCountryId = :regionCountryId"

    )
    List<ProductMain> getProductBySeoName(
            @Param("seoName") List<String> seoName,
            @Param("regionCountryId") String regionCountryId

    );
    
    @Query(value = "SELECT COUNT(*) AS bil, A.itemCode, "
            + "D.id " +
        "FROM `order_item` A " +
        "	INNER JOIN `order` B ON A.orderId=B.id " +
        "	INNER JOIN `product_inventory` C ON A.itemCode=C.itemCode " +
        "	INNER JOIN `product` D ON C.productId=D.id " +
        "WHERE B.storeId=:storeId AND D.status='ACTIVE' " +
        "GROUP BY itemcode " +
        "ORDER BY bil DESC " +
        "LIMIT :limit", nativeQuery = true)
    List<Object[]> getFamousItemByStoreId(@Param("storeId") String storeId, int limit);
    

}

