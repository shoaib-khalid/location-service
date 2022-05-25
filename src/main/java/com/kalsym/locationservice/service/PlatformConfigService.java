package com.kalsym.locationservice.service;

import java.util.List;
import java.util.Optional;

import com.kalsym.locationservice.model.PlatformConfig;
import com.kalsym.locationservice.repository.PlatformConfigRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlatformConfigService {
    
    @Autowired
    PlatformConfigRepository platformConfigRepository; 

    // READ
    public List<PlatformConfig> getPlatformConfigs() {
        
        List<PlatformConfig> result =platformConfigRepository.findAll();
        System.out.println("Checking body :::"+result);

        return result;
    }

    // READ by id
    public Optional<PlatformConfig> getPlatformConfigId(String platformId){
    return platformConfigRepository.findById(platformId);
    }
}
