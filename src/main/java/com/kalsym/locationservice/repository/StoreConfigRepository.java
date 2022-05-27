package com.kalsym.locationservice.repository;


import com.kalsym.locationservice.model.Config.StoreConfig;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreConfigRepository extends JpaRepository<StoreConfig,Integer> {
    
}

