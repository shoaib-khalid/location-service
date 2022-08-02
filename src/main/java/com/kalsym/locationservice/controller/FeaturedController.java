package com.kalsym.locationservice.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.kalsym.locationservice.model.Config.LocationConfig;
import com.kalsym.locationservice.model.Config.ProductFeatureConfig;
import com.kalsym.locationservice.model.Config.StoreConfig;
import com.kalsym.locationservice.model.Product.ProductMain;
import com.kalsym.locationservice.service.CategoryLocationService;
import com.kalsym.locationservice.service.LocationConfigService;
import com.kalsym.locationservice.service.ProductService;
import com.kalsym.locationservice.service.StoreConfigService;
import com.kalsym.locationservice.utility.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;


@RestController
@RequestMapping("/featured")
public class FeaturedController {
    
    @Autowired
    LocationConfigService locationConfigService;

    @Autowired
    StoreConfigService storeConfigService;

    @Autowired
    ProductService productService;

    @GetMapping(path = {"/location"}, name = "store-customers-get")
    @PreAuthorize("hasAnyAuthority('store-customers-get', 'all')")
    public ResponseEntity<HttpResponse> getLocationConfig(
        HttpServletRequest request,
        @RequestParam(required = false) String cityId,
        @RequestParam(required = false) String regionCountryId,
        @RequestParam(required = false) String cityName,
        @RequestParam(required = false, defaultValue = "cityId") String sortByCol,
        @RequestParam(required = false, defaultValue = "ASC") Sort.Direction sortingOrder,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int pageSize
    ) {

        Page<LocationConfig> body = locationConfigService.getQueryLocationConfig(cityId,regionCountryId,cityName,sortByCol,sortingOrder,page,pageSize);
        
        HttpResponse response = new HttpResponse(request.getRequestURI());
        response.setData(body);
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);

    }

    
    //Create
    @PostMapping(path = {"/location"}, name = "region-countries-post")
    @PreAuthorize("hasAnyAuthority('region-countries-post', 'all')")
    public ResponseEntity<HttpResponse> postLocationConfig(
        HttpServletRequest request,
        @RequestBody LocationConfig locationConfigBody) throws Exception {


            HttpResponse response = new HttpResponse(request.getRequestURI());

            try{
                LocationConfig body = locationConfigService.createLocationConfig(locationConfigBody);
                response.setData(body);
                response.setStatus(HttpStatus.OK);

            } catch (Throwable e) {
                // response.setStatus(HttpStatus.BAD_REQUEST);

            }     
 
        return ResponseEntity.status(response.getStatus()).body(response);


    }

    @GetMapping(path = {"/store"}, name = "store-customers-get")
    @PreAuthorize("hasAnyAuthority('store-customers-get', 'all')")
    public ResponseEntity<HttpResponse> getDisplayStoreConfig(
        HttpServletRequest request,
        @RequestParam(required = false) String regionCountryId,
        @RequestParam(required = false) List<String> cityId,
        @RequestParam(required = false) String cityName,
        @RequestParam(required = false) String parentCategoryId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(required = false,defaultValue = "sequence") String sortByCol,
        @RequestParam(required = false,defaultValue = "ASC") Sort.Direction sortingOrder
    ) {

        HttpResponse response = new HttpResponse(request.getRequestURI());

        try{
                    // Page<StoreConfig> body = storeConfigService.getQueryStoreConfig(regionCountryId,cityId,cityName,parentCategoryId,page,pageSize,sortByCol,sortingOrder);
        Page<StoreConfig> body = storeConfigService.getRawQueryStoreConfig(regionCountryId,cityId,cityName,parentCategoryId,page,pageSize,sortByCol,sortingOrder);
        response.setData(body);
        response.setStatus(HttpStatus.OK);

        }catch(Throwable e){

                System.out.println("Error message:::::::::::::::::"+e.getMessage());
                response.setStatus(HttpStatus.EXPECTATION_FAILED);//error code 417

        }

  
        return ResponseEntity.status(response.getStatus()).body(response);

    }

    @GetMapping(path = {"/product"}, name = "store-customers-get")
    @PreAuthorize("hasAnyAuthority('store-customers-get', 'all')")
    public ResponseEntity<HttpResponse> getFeaturedProducts(
        HttpServletRequest request,
        @RequestParam(required = false) List<String> status,
        @RequestParam(required = false) String regionCountryId,
        @RequestParam(required = false) String parentCategoryId,
        @RequestParam(required = false) List<String> cityId,
        @RequestParam(required = false) String cityName,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Boolean isMainLevel,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(required = false) String sortByCol,
        @RequestParam(required = false) Sort.Direction sortingOrder
    ) {

        HttpResponse response = new HttpResponse(request.getRequestURI());

        try{
            Page<ProductFeatureConfig> body = productService.getFeaturedProductWithLocationParentCategory(status, regionCountryId, parentCategoryId, cityId, cityName, name, isMainLevel,page, pageSize, sortByCol, sortingOrder );
            response.setData(body);
            response.setStatus(HttpStatus.OK);

        }catch(Throwable e){
            System.out.println("Error message:::::::::::::::::"+e.getMessage());

            response.setStatus(HttpStatus.EXPECTATION_FAILED);//error code 417

        }
       
        return ResponseEntity.status(response.getStatus()).body(response);

    }

    
}
