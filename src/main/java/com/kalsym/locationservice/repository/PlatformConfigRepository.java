package com.kalsym.locationservice.repository;

import java.util.List;

import com.kalsym.locationservice.model.PlatformConfig;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// @Transactional
public interface PlatformConfigRepository extends JpaRepository<PlatformConfig,String> {

    //Note that if we want to use Raw Query u need to use Model Name instad of table name
    // public List<PlatformConfig> findAll();
    List<PlatformConfig> findByDomain(String domain);

}
