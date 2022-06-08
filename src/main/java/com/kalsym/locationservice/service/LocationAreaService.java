package com.kalsym.locationservice.service;

import com.kalsym.locationservice.model.LocationArea;

import com.kalsym.locationservice.repository.LocationAreaRepository;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class LocationAreaService {

    @Autowired
    LocationAreaRepository locationAreaRepository;
    

    public List<LocationArea> getQueryLocationArea(String userLocationCityId){
     
        Collection<LocationArea> result = locationAreaRepository.getLocationAreaQuery(userLocationCityId);

        List<LocationArea> output = new ArrayList<LocationArea>(result);

        return output;

 
    }
    
     
}