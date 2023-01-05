package com.kalsym.locationservice.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kalsym.locationservice.model.TagTable;
@Repository

public interface TagTableRepository extends JpaRepository<TagTable,Integer> {
    
    List<TagTable> findByZoneId(@Param("zoneId") Integer storeId);

}
