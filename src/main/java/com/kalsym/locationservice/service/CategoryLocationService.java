package com.kalsym.locationservice.service;


import java.util.List;

import com.kalsym.locationservice.model.Category;
import com.kalsym.locationservice.model.ParentCategory;
import com.kalsym.locationservice.model.Store;
// import com.kalsym.locationservice.model.CategoryLocation;
// import com.kalsym.locationservice.model.LocationCategory;
// import com.kalsym.locationservice.repository.CategoryLocationRepository;
// import com.kalsym.locationservice.repository.LocationCategoryRepository;
import com.kalsym.locationservice.repository.CategoryRepository;
import com.kalsym.locationservice.repository.ParentCategoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Example;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatcher;


import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

@Service
public class CategoryLocationService {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ParentCategoryRepository parentCategoryRepository;
    
    //Get By Query WITH Pagination
    //Child category 
    public Page<Category> getQueryChildCategory(String city, String stateId,String regionCountryId, String postcode, String parentCategoryId, String sortByCol, Sort.Direction sortingOrder,int page, int pageSize){
    
        Store storeMatch = new Store();
        storeMatch.setCity(city);
        storeMatch.setState(stateId);
        storeMatch.setRegionCountryId(regionCountryId);
        storeMatch.setPostcode(postcode);


        ParentCategory parentCategoryMatch = new ParentCategory();
        parentCategoryMatch.setParentId(parentCategoryId);
       
        Category categoryMatch = new Category();
        categoryMatch.setStoreDetails(storeMatch);
        categoryMatch.setParentCategory(parentCategoryMatch);

        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withMatcher("city", new GenericPropertyMatcher().exact())
                .withMatcher("stateId", new GenericPropertyMatcher().exact())
                .withMatcher("regionCountryId", new GenericPropertyMatcher().exact())
                .withMatcher("postcode", new GenericPropertyMatcher().exact())
                .withMatcher("parentCategoryId", new GenericPropertyMatcher().exact())
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Category> example = Example.of(categoryMatch, matcher);

        Pageable pageable;
   
        if (sortingOrder==Sort.Direction.DESC){
            pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).descending());
        }
        else{
            pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).ascending());
        }
        
        return categoryRepository.findAll(example,pageable);

    }
    
    //parent category
    public List<Object> getQueryParentCategories(String city, String stateId, String regionCountryId, String postcode){

        List<Object[]> result = categoryRepository.getParentCategoriesBasedOnLocation(stateId,city,postcode,regionCountryId);
        // System.out.println("Checking result 1 :::"+result.toString());
        List<Object> parentCategoriesList = result.stream()
        .map(m -> {
            // System.out.println("Checking m[0] :::"+m[2]);
            ParentCategory parentCategoryList = new ParentCategory();
            parentCategoryList.setParentId(m[0].toString());
            parentCategoryList.setParentName(m[1].toString());
            parentCategoryList.setParentThumbnailUrl(m[2]== null?"":m[2].toString());
            return parentCategoryList;
        })
        .collect(Collectors.toList());

        return parentCategoriesList ;
    }

    public Page<Category> getQueryStore(String city, String cityName, String stateId,String regionCountryId, String postcode, String parentCategoryId, String storeName, int page, int pageSize){
    
        //Handling null value in order to use query
        if (city == null || city.isEmpty()) {
            city = "";
        }

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

        if (storeName == null || storeName.isEmpty()) {
            storeName = "";
        }

        Pageable pageable = PageRequest.of(page, pageSize);

        //find the based on location with pageable
        Page<Category> result = categoryRepository.getStoreBasedOnParentCategories(city,cityName,stateId,regionCountryId,postcode,parentCategoryId,storeName,pageable);
   
        // if (sortingOrder==Sort.Direction.DESC){
        //     pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).descending());
        // }
        // else{
        //     pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).ascending());
        // }
        
        // return categoryRepository.findAll(example,pageable);

        return result;
    }

    public Page<ParentCategory> getQueryParentCategoriesBasedOnLocation(String city, String stateId, String regionCountryId, String postcode, String parentCategoryId, String sortByCol,Sort.Direction sortingOrder, int page, int pageSize){

        if (parentCategoryId == null || parentCategoryId.isEmpty()) {
            parentCategoryId = "";
        }

        Pageable pageable;

        if (sortingOrder==Sort.Direction.DESC){
            pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).descending());
        }
        else if(sortingOrder==Sort.Direction.ASC){
            pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).ascending());
        }
        else{
            pageable = PageRequest.of(page, pageSize);
        }

        Page<ParentCategory> result = parentCategoryRepository.getParentCategoriesBasedOnLocationQuery(stateId,city,postcode,regionCountryId,parentCategoryId,pageable);

        return result;
    }

     
}
