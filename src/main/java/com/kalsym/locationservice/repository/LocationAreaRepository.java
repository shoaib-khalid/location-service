package com.kalsym.locationservice.repository;


import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kalsym.locationservice.model.LocationArea;

@Repository
public interface LocationAreaRepository extends JpaRepository<LocationArea,String> {
    
    @Query(
        value =
        " SELECT * "
        +"FROM location_area la "
        +"WHERE userLocationCityId = :userLocationCityId "
        +"ORDER BY storeCityId ASC "
        +"LIMIT 5", 
        nativeQuery = true
    )
    Collection<LocationArea> getLocationAreaQuery(
        @Param("userLocationCityId") String userLocationCityId
    );
   
}

