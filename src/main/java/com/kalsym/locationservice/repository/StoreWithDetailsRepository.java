package com.kalsym.locationservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.kalsym.locationservice.model.StoreWithDetails;

@Repository
public interface StoreWithDetailsRepository extends JpaRepository<StoreWithDetails,String>, JpaSpecificationExecutor<StoreWithDetails> {
    
}
