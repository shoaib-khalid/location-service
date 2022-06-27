package com.kalsym.locationservice.repository;


import java.util.Collection;
import java.util.List;

import com.kalsym.locationservice.model.Category;
import com.kalsym.locationservice.model.CustomerActivitiesSummary;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;



@Repository
public interface CustomerActivitiesSummaryRepository extends JpaRepository<CustomerActivitiesSummary,Integer> {
    
    //Note that if we want to use Raw Query , need to specify nativeQuery = true, 
    //if you dont specify then make sure the table name u use model name.

    List<CustomerActivitiesSummary> findByStoreId(@Param("storeId") String storeId);

    @Query(
        value =
        " SELECT cs.id, cs.page , cs.storeId, SUBSTRING_INDEX(cs.page, '/', -1) as subs , SUM(cs.totalCount) as total "
        +"FROM symplified_analytic.customer_activities_summary cs "
        +"INNER JOIN symplified.store s on s.id = cs.storeId "
        +"AND cs.page LIKE '%all-products%' "
        +"AND cs.page NOT  LIKE '%all-products' "
        +"AND s.regionCountryId = :regionCountryId "
        +"GROUP by subs "
        +"ORDER BY total DESC "
        +"LIMIT 20", 
        nativeQuery = true
    )
    Collection<CustomerActivitiesSummary> getTrendingProducts(
        @Param("regionCountryId") String regionCountryId
    );

    @Query(
        value =
        " SELECT cs.id, cs.page , cs.storeId, SUBSTRING_INDEX(cs.page, '/', -1) as subs , SUM(cs.totalCount) as total "
        +"FROM symplified_analytic.customer_activities_summary cs "
        +"INNER JOIN symplified.store s on s.id = cs.storeId "
        +"AND cs.page LIKE '%all-products%' "
        +"AND cs.page NOT  LIKE '%all-products' "
        +"AND cs.page NOT  LIKE '%all-products?%' "
        +"AND s.regionCountryId = :regionCountryId "
        +"GROUP by subs "
        +"ORDER BY total DESC "
        +"LIMIT 20", 
        nativeQuery = true
    )
    List<Object[]> getTrendingProductsObject(
        @Param("regionCountryId") String regionCountryId
    );

    //combine both url marketplace and store front for product page only
    //RAW QUERY 
    // SELECT cs.page , cs.storeId, SUBSTRING_INDEX(cs.page, '/', -1) as subs , SUM(cs.totalCount) as total
    // from symplified_analytic.customer_activities_summary cs
    // inner join symplified.store s on s.id = cs.storeId
    // and cs.page LIKE '%all-products%'
    // and cs.page NOT  LIKE '%all-products'
    // and s.regionCountryId ='MYS'
    // GROUP by subs
    // ORDER BY total DESC
    // limit 20

}

