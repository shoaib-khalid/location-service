package com.kalsym.locationservice.service;

import java.util.ArrayList;
import java.util.List;

import com.kalsym.locationservice.model.Category;
import com.kalsym.locationservice.model.ParentCategory;
import com.kalsym.locationservice.model.Store;
import com.kalsym.locationservice.model.Product.ProductMain;
import com.kalsym.locationservice.repository.ProductRepository;

import org.hibernate.dialect.MySQL55Dialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Example;

import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatcher;


import org.springframework.stereotype.Service;
import java.util.stream.Collectors;
@Service
public class ProductService {
    
    @Autowired
    ProductRepository productRepository;

    //Get By Query USING EXAMPLE MATCHER
    public Page<ProductMain> getQueryProduct(String city, String stateId,String regionCountryId, String postcode, String status, String sortByCol, Sort.Direction sortingOrder,int page, int pageSize){
    
        Store storeMatch = new Store();
        storeMatch.setCity(city);
        storeMatch.setState(stateId);
        storeMatch.setRegionCountryId(regionCountryId);
        storeMatch.setPostcode(postcode);
       
        ProductMain productMainMatch = new ProductMain();
        productMainMatch.setStatus(status);
        productMainMatch.setStoreDetails(storeMatch);
        // System.out.println("IMAN CHECKING ::::::::::::::::::::::::::::"+productMainMatch.getStatus().toString());

        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withMatcher("city", new GenericPropertyMatcher().exact())
                .withMatcher("stateId", new GenericPropertyMatcher().exact())
                .withMatcher("regionCountryId", new GenericPropertyMatcher().exact())
                .withMatcher("postcode", new GenericPropertyMatcher().exact())
                .withMatcher("status", new GenericPropertyMatcher().exact())
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        System.out.println("Checking matcher :::"+matcher);

        Example<ProductMain> example = Example.of(productMainMatch, matcher);

        System.out.println("Checking example :::"+example);

        Pageable pageable;
   
        if (sortingOrder==Sort.Direction.DESC){
            pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).descending());
        }
        else{
            pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).ascending());
        }
        
        return productRepository.findAll(example,pageable);

    }

    public Page<ProductMain> getRawQueryProduct(String city, String stateId,String regionCountryId, String postcode, List<String> status, String sortByCol, Sort.Direction sortingOrder,int page, int pageSize){
    
        //Handling null value in order to use query
        if (regionCountryId == null || regionCountryId.isEmpty()) {
            regionCountryId = "";
        }

        if (stateId == null || stateId.isEmpty()) {
            stateId = "";
        }

        if (city == null || city.isEmpty()) {
            city = "";
        }

        if (postcode == null || postcode.isEmpty()) {
            postcode = "";
        }

        if (status == null) {

            List<String> statusList = new ArrayList<>();
            statusList.add("ACTIVE");
            statusList.add("INACTIVE");
            statusList.add("OUTOFSTOCK");

            status = statusList;
        }

        Pageable pageable;

        if (sortingOrder==Sort.Direction.DESC){
            pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).descending());
        }
        else{
            pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).ascending());
        }
        Page<ProductMain> result = productRepository.getProductBasedOnLocation(status,stateId,regionCountryId,city,postcode,pageable);

        return result;
    }
}
