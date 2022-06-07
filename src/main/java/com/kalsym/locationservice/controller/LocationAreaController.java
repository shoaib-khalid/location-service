package com.kalsym.locationservice.controller;

import java.util.List;


import javax.servlet.http.HttpServletRequest;

import com.kalsym.locationservice.model.LocationArea;
import com.kalsym.locationservice.service.LocationAreaService;
import com.kalsym.locationservice.utility.HttpResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Sort;


@RestController
@RequestMapping("")
public class LocationAreaController {
    
    @Autowired
    LocationAreaService locationAreaService;

    @GetMapping(path = {"/location-area"}, name = "store-customers-get")
    @PreAuthorize("hasAnyAuthority('store-customers-get', 'all')")
    public ResponseEntity<HttpResponse> getLocationArea(
        HttpServletRequest request,
        @RequestParam(required = true) String userLocationCityId,
        @RequestParam(required = false, defaultValue = "userLocationCityId") String sortByCol,
        @RequestParam(required = false, defaultValue = "ASC") Sort.Direction sortingOrder
    ) {

        List<LocationArea> body = locationAreaService.getQueryLocationArea(userLocationCityId,sortByCol,sortingOrder);
        
        HttpResponse response = new HttpResponse(request.getRequestURI());
        response.setData(body);
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);

    }

}

