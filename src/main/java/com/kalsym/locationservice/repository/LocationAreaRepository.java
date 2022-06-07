package com.kalsym.locationservice.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kalsym.locationservice.model.LocationArea;

@Repository
public interface LocationAreaRepository extends JpaRepository<LocationArea,String> {
    
}

