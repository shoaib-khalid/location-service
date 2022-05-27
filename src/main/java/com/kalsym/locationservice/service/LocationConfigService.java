package com.kalsym.locationservice.service;

import java.util.ArrayList;
import java.util.List;

import com.kalsym.locationservice.model.RegionCity;
import com.kalsym.locationservice.model.Store;
import com.kalsym.locationservice.model.Config.LocationConfig;
import com.kalsym.locationservice.model.Product.ProductMain;
import com.kalsym.locationservice.repository.LocationConfigRepository;
import com.kalsym.locationservice.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatcher;
import org.springframework.stereotype.Service;

@Service
public class LocationConfigService {
    
    @Autowired
    LocationConfigRepository locationConfigRepository;

       //Get By Query USING EXAMPLE MATCHER
       public Page<LocationConfig> getQueryLocationConfig(String cityId,Boolean isDisplay, String sortByCol, Sort.Direction sortingOrder,int page, int pageSize){
       
        LocationConfig LocationConfigMatch = new LocationConfig();
        LocationConfigMatch.setCityId(cityId);
        LocationConfigMatch.setIsDisplay(isDisplay);
        
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withMatcher("cityId", new GenericPropertyMatcher().exact())
                .withMatcher("isDisplay", new GenericPropertyMatcher().exact())
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        Example<LocationConfig> example = Example.of(LocationConfigMatch, matcher);

        Pageable pageable;
   
        if (sortingOrder==Sort.Direction.DESC){
            pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).descending());
        }
        else{
            pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).ascending());
        }
        
        return locationConfigRepository.findAll(example,pageable);

    }

    // CREATE 
    public LocationConfig createLocationConfig(LocationConfig locationConfig){
        

        // RegionCity citydata = new RegionCity();
        // citydata.setId(id);

        // LocationConfig data = locationConfig;
        // data.setCityId(data.getCityId());
        // data.setIsDisplay(data.getIsDisplay());
        
        return locationConfigRepository.save(locationConfig);
    }

}
