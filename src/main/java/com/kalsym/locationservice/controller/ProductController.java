package com.kalsym.locationservice.controller;

import java.util.List;


import javax.servlet.http.HttpServletRequest;

import com.kalsym.locationservice.model.CustomerActivitiesSummary;
import com.kalsym.locationservice.model.Product.ProductMain;
import com.kalsym.locationservice.service.CategoryLocationService;
import com.kalsym.locationservice.LocationServiceApplication;
import com.kalsym.locationservice.service.ProductService;
import com.kalsym.locationservice.utility.HttpResponse;
import com.kalsym.locationservice.utility.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@RequestMapping("")
public class ProductController {
    
    @Autowired
    ProductService productService;
    
    @Value("${product.search.radius}")
    private Double searchRadius;
    
    @GetMapping(path = {"/products"}, name = "store-customers-get")
    @PreAuthorize("hasAnyAuthority('store-customers-get', 'all')")
    public ResponseEntity<HttpResponse> getProducts(
        HttpServletRequest request,
        @RequestParam(required = true) String regionCountryId,
        @RequestParam(required = false) String parentCategoryId,
        @RequestParam(required = false) List<String> cityId,
        @RequestParam(required = false) String cityName,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) List<String> status,
        @RequestParam(required = false) String latitude,
        @RequestParam(required = false) String longitude,
        @RequestParam(required = false) String storeTagKeyword,
        @RequestParam(required = false) Boolean isMainLevel,
        @RequestParam(required = false) Boolean isDineIn,
        @RequestParam(required = false) Boolean isDelivery,
        @RequestParam(required = false) Boolean showAllPrice,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(required = false, defaultValue = "created") String sortByCol,
        @RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortingOrder
    ) {
        //to set default filter
        if(isDelivery == null && isDineIn == null){
            isDelivery = true;
        }

        //to hide the product that 0 price
        if(showAllPrice == null ){
            showAllPrice = false;
        }

        String logprefix = "getProducts()";
        Logger.application.info(Logger.pattern, LocationServiceApplication.VERSION, logprefix, "get-products request...");
        
        Page<ProductMain> body = productService.getQueryProductByParentCategoryIdAndLocation(status,regionCountryId,parentCategoryId,cityId,cityName,name,latitude,longitude,searchRadius,storeTagKeyword,isMainLevel,isDineIn,isDelivery,showAllPrice,page,pageSize,sortByCol,sortingOrder);
        
        Logger.application.info(Logger.pattern, LocationServiceApplication.VERSION, logprefix, "get-products result : "+body.toString());
       
        HttpResponse response = new HttpResponse(request.getRequestURI());
        response.setData(body);
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);

    }

    @GetMapping(path = {"/trending-products"}, name = "store-customers-get")
    @PreAuthorize("hasAnyAuthority('store-customers-get', 'all')")
    public ResponseEntity<HttpResponse> getTrendingProducts(
        HttpServletRequest request,
        @RequestParam(required = true) String regionCountryId
     
    ) {

        List<ProductMain> body = productService.getCustomerActivities(regionCountryId);
        
        HttpResponse response = new HttpResponse(request.getRequestURI());
        response.setData(body);
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);

    }

}
