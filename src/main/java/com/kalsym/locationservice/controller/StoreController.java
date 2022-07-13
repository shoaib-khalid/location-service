package com.kalsym.locationservice.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

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


@RestController
@RequestMapping("")
public class StoreController {
    
    @Autowired
    CategoryLocationService categoryLocationService;

    @GetMapping(path = {"/stores"}, name = "store-customers-get")
    @PreAuthorize("hasAnyAuthority('store-customers-get', 'all')")
    public ResponseEntity<HttpResponse> getStores(
        HttpServletRequest request,
        @RequestParam(required = false) List<String> cityId,
        @RequestParam(required = false) String cityName,
        @RequestParam(required = false) String stateId,
        @RequestParam(required = true) String regionCountryId,
        @RequestParam(required = false) String postcode,
        @RequestParam(required = false) String storeName,
        @RequestParam(required = false) String parentCategoryId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int pageSize
    ) {

        Page<StoreCategory> body = categoryLocationService.getQueryStore(cityId,cityName,stateId,regionCountryId,postcode,parentCategoryId,storeName,page,pageSize);
        
        HttpResponse response = new HttpResponse(request.getRequestURI());
        response.setData(body);
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);

    }

}
