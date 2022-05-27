package com.kalsym.locationservice.repository;


import com.kalsym.locationservice.model.Config.LocationConfig;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationConfigRepository extends JpaRepository<LocationConfig,String> {
    
}
