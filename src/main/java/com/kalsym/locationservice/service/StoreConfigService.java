package com.kalsym.locationservice.service;

import com.kalsym.locationservice.model.Category;
import com.kalsym.locationservice.model.RegionCity;
import com.kalsym.locationservice.model.Store;
import com.kalsym.locationservice.model.Config.LocationConfig;
import com.kalsym.locationservice.model.Config.StoreConfig;
import com.kalsym.locationservice.repository.CategoriesSearchSpecs;
import com.kalsym.locationservice.repository.LocationConfigRepository;
import com.kalsym.locationservice.repository.StoreConfigRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatcher;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class StoreConfigService {
    
    @Autowired
    StoreConfigRepository storeConfigRepository;
    
    //Get By Query USING EXAMPLE MATCHER for 
    public Page<StoreConfig> getQueryStoreConfig(String regionCountryId, String cityId, String cityName, String parentCategoryId, int page, int pageSize, String sortByCol, Sort.Direction sortingOrder){
       
        RegionCity regionCityMatch = new RegionCity();
        regionCityMatch.setName(cityName);

        Category categoryMatch = new Category();
        categoryMatch.setParentCategoryId(parentCategoryId);
    
        Store storeMatch = new Store();
        storeMatch.setRegionCountryId(regionCountryId);
        storeMatch.setCity(cityId);
        storeMatch.setRegionCityDetails(regionCityMatch);

        StoreConfig storeConfigMatch = new StoreConfig();
        storeConfigMatch.setStoreDetails(storeMatch);

        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withMatcher("regionCountryId", new GenericPropertyMatcher().exact())
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        Example<StoreConfig> example = Example.of(storeConfigMatch, matcher);

        // Specification<StoreConfig> categoriesSpec = CategoriesSearchSpecs.getSpecWithDatesBetween(parentCategoryId, example );


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

    public Page<StoreConfig> getRawQueryStoreConfig(String regionCountryId, List<String> cityId, String cityName, String parentCategoryId, int page, int pageSize, String sortByCol, Sort.Direction sortingOrder){
    
        //Handling null value in order to use query
        // if (cityId == null || cityId.isEmpty()) {
        //     cityId = "";
        // }

        // if (stateId == null || stateId.isEmpty()) {
        //     stateId = "";
        // }
        if (cityName == null || cityName.isEmpty()) {
            cityName = "";
        }

        if (regionCountryId == null || regionCountryId.isEmpty()) {
            regionCountryId = "";
        }

        // if (postcode == null || postcode.isEmpty()) {
        //     postcode = "";
        // }

        if (parentCategoryId == null || parentCategoryId.isEmpty()) {
            parentCategoryId = "";
        }


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

        //find the based on location with pageable
        Page<StoreConfig> result = storeConfigRepository.getQueryStoreConfigRaw(cityId,cityName,regionCountryId,parentCategoryId,pageable);

        
        // return categoryRepository.findAll(example,pageable);

        return result;
    }



}
