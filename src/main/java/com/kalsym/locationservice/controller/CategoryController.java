package com.kalsym.locationservice.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.kalsym.locationservice.model.ParentCategory;
import com.kalsym.locationservice.model.StoreCategory;
import com.kalsym.locationservice.service.CategoryLocationService;
import com.kalsym.locationservice.utility.HttpResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;


@RestController
@RequestMapping("/categories")
public class CategoryController {
    
    @Autowired
    CategoryLocationService categoryLocationService;

    @GetMapping(path = {"/child-category"}, name = "store-customers-get")
    @PreAuthorize("hasAnyAuthority('store-customers-get', 'all')")
    public ResponseEntity<HttpResponse> getByQueryChildCategory(
        HttpServletRequest request,
        @RequestParam(required = false) String cityId,
        @RequestParam(required = false) String stateId,
        @RequestParam(required = false) String regionCountryId,
        @RequestParam(required = false) String postcode,
        @RequestParam(required = false) String parentCategoryId,
        @RequestParam(required = false, defaultValue = "name") String sortByCol,
        @RequestParam(required = false, defaultValue = "ASC") Sort.Direction sortingOrder,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int pageSize
    ) {

        Page<StoreCategory> body = categoryLocationService.getQueryChildCategory(cityId,stateId,regionCountryId,postcode,parentCategoryId,sortByCol,sortingOrder,page,pageSize);
        
        HttpResponse response = new HttpResponse(request.getRequestURI());
        response.setData(body);
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);

    }

    @GetMapping(path = {"/parent-category"}, name = "store-customers-get")
    @PreAuthorize("hasAnyAuthority('store-customers-get', 'all')")
    public ResponseEntity<HttpResponse> getParentCategory(
        HttpServletRequest request,
        @RequestParam(required = false) List<String> cityId,
        @RequestParam(required = false) String stateId,
        @RequestParam(required = false) String regionCountryId,
        @RequestParam(required = false) String postcode,
        @RequestParam(required = false) String parentCategoryId,
        @RequestParam(required = false) String sortByCol,
        @RequestParam(required = false) Sort.Direction sortingOrder,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int pageSize
    ) {

        Page<ParentCategory> body = categoryLocationService.getQueryParentCategoriesBasedOnLocation(cityId,stateId,regionCountryId,postcode,parentCategoryId,sortByCol,sortingOrder,page,pageSize);
        
        HttpResponse response = new HttpResponse(request.getRequestURI());
        response.setData(body);
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);

    }

 
}
