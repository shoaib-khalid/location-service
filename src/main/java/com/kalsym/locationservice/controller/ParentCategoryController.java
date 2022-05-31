package com.kalsym.locationservice.controller;

import java.util.List;


import javax.servlet.http.HttpServletRequest;

import com.kalsym.locationservice.model.Category;
import com.kalsym.locationservice.model.Product.ProductMain;
import com.kalsym.locationservice.service.CategoryLocationService;
import com.kalsym.locationservice.service.ProductService;
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
@RequestMapping("/parent-category")
public class ParentCategoryController {
    
    @Autowired
    CategoryLocationService categoryLocationService;

    @Autowired
    ProductService productService;

    @GetMapping(path = {"/stores"}, name = "store-customers-get")
    @PreAuthorize("hasAnyAuthority('store-customers-get', 'all')")
    public ResponseEntity<HttpResponse> getByQueryStoreList(
        HttpServletRequest request,
        @RequestParam(required = false) String city,
        @RequestParam(required = false) String stateId,
        @RequestParam(required = false) String regionCountryId,
        @RequestParam(required = false) String postcode,
        @RequestParam(required = false) String parentCategoryId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int pageSize
    ) {

        Page<Category> body = categoryLocationService.getQueryStore(city,stateId,regionCountryId,postcode,parentCategoryId,page,pageSize);
        
        HttpResponse response = new HttpResponse(request.getRequestURI());
        response.setData(body);
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);

    }

    @GetMapping(path = {"/products"}, name = "store-customers-get")
    @PreAuthorize("hasAnyAuthority('store-customers-get', 'all')")
    public ResponseEntity<HttpResponse> getByQueryProductList(
        HttpServletRequest request,
        @RequestParam(required = false) String regionCountryId,
        @RequestParam(required = false) String parentCategoryId,
        @RequestParam(required = false) List<String> status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int pageSize
    ) {

        Page<ProductMain> body = productService.getQueryProductByParentCategoryIdAndLocation(status,regionCountryId,parentCategoryId,page,pageSize);
        
        HttpResponse response = new HttpResponse(request.getRequestURI());
        response.setData(body);
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);

    }


}
