package com.kalsym.locationservice.service;

import com.kalsym.locationservice.model.Store;
import com.kalsym.locationservice.model.Config.LocationConfig;
import com.kalsym.locationservice.model.Config.StoreConfig;
import com.kalsym.locationservice.repository.LocationConfigRepository;
import com.kalsym.locationservice.repository.StoreConfigRepository;

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
public class StoreConfigService {
    
    @Autowired
    StoreConfigRepository storeConfigRepository;
    
    //Get By Query USING EXAMPLE MATCHER for 
    public Page<StoreConfig> getQueryStoreConfig(Boolean isDisplay, String regionCountryId, int page, int pageSize, String sortByCol, Sort.Direction sortingOrder){
       
        Store storeMatch = new Store();
        storeMatch.setRegionCountryId(regionCountryId);

        StoreConfig storeConfigMatch = new StoreConfig();
        storeConfigMatch.setIsDisplay(isDisplay);
        storeConfigMatch.setStoreDetails(storeMatch);

        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withMatcher("isDisplay", new GenericPropertyMatcher().exact())
                .withMatcher("regionCountryId", new GenericPropertyMatcher().exact())
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        Example<StoreConfig> example = Example.of(storeConfigMatch, matcher);

        Pageable pageable;
   
        if (sortingOrder==Sort.Direction.DESC){
            pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).descending());
        }
        else if (sortingOrder==Sort.Direction.ASC){
            pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).ascending());
        }
        else{
            pageable = PageRequest.of(page, pageSize);
        }

        return storeConfigRepository.findAll(example,pageable);

    }



}
