package com.kalsym.locationservice.service;

import com.kalsym.locationservice.model.LocationArea;

import com.kalsym.locationservice.repository.LocationAreaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Example;

import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatcher;


import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationAreaService {

    @Autowired
    LocationAreaRepository locationAreaRepository;
    

    public List<LocationArea> getQueryLocationArea(String userLocationCityId,String sortByCol, Sort.Direction sortingOrder){
    
        LocationArea locationAreaMatch = new LocationArea();
        locationAreaMatch.setUserLocationCityId(userLocationCityId);

        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<LocationArea> example = Example.of(locationAreaMatch, matcher);

        Sort sort;

        if (sortingOrder==Sort.Direction.DESC){
            sort = Sort.by(sortByCol).descending();
        }
        else{
            sort = Sort.by(sortByCol).ascending();//Default ascending
        }
        
        return locationAreaRepository.findAll(example,sort);

    }
    
     
}