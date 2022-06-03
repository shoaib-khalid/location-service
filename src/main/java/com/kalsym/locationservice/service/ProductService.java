package com.kalsym.locationservice.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.kalsym.locationservice.enums.DiscountCalculationType;
import com.kalsym.locationservice.model.RegionCountry;
import com.kalsym.locationservice.model.Store;
import com.kalsym.locationservice.model.Config.ProductFeatureConfig;
import com.kalsym.locationservice.model.Discount.StoreDiscountProduct;
import com.kalsym.locationservice.model.Product.ItemDiscount;
import com.kalsym.locationservice.model.Product.ProductInventoryWithDetails;
import com.kalsym.locationservice.model.Product.ProductMain;
import com.kalsym.locationservice.repository.GetDiscount;
import com.kalsym.locationservice.repository.ProductFeaturedRepository;
import com.kalsym.locationservice.repository.ProductRepository;
import com.kalsym.locationservice.repository.RegionCountriesRepository;
import com.kalsym.locationservice.repository.StoreDiscountProductRepository;
import com.kalsym.locationservice.repository.StoreDiscountRepository;
import com.kalsym.locationservice.utility.ProductDiscount;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatcher;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    
    @Autowired
    ProductRepository productRepository;

    @Autowired
    RegionCountriesRepository regionCountriesRepository;

    @Autowired
    StoreDiscountRepository storeDiscountRepository;

    @Autowired
    StoreDiscountProductRepository storeDiscountProductRepository;

    @Autowired
    ProductFeaturedRepository productFeaturedRepository;

    public Page<ProductMain> getQueryProductByParentCategoryIdAndLocation(List<String> status,String regionCountryId,String parentCategoryId, String cityId, String cityName, String name,int page, int pageSize){

        //Handling null value in order to use query
        if (regionCountryId == null || regionCountryId.isEmpty()) {
            regionCountryId = "";
        }

        if (cityId == null || cityId.isEmpty()) {
            cityId = "";
        }

        if (cityName == null || cityName.isEmpty()) {
            cityName = "";
        }

        if (name == null || name.isEmpty()) {
            name = "";
        }

        if (parentCategoryId == null || parentCategoryId.isEmpty()) {
            parentCategoryId = "";
        }

        if (status == null) {

            List<String> statusList = new ArrayList<>();
            statusList.add("ACTIVE");
            statusList.add("INACTIVE");
            statusList.add("OUTOFSTOCK");

            status = statusList;
        }

        Pageable pageable = PageRequest.of(page, pageSize);

        //get reqion country for store
        RegionCountry regionCountry = null;
        Optional<RegionCountry> optRegion = regionCountriesRepository.findById(regionCountryId);
        if (optRegion.isPresent()) {
            regionCountry = optRegion.get();
        }

        //find the based on location with pageable
        Page<ProductMain> result = productRepository.getProductByParentCategoryIdAndLocation(status,regionCountryId,parentCategoryId,cityId,cityName,name,pageable);

        //extract the result of content of pageable in order to proceed with dicount of item 
        List<ProductMain> productList = result.getContent();

        // to get discount of product
        ProductMain[] productWithDetailsList = GetDiscount.getProductDiscountList(productList, regionCountry, storeDiscountRepository, storeDiscountProductRepository);

        // convert array to array list
        List<ProductMain> newArrayList = new ArrayList<>(Arrays.asList(productWithDetailsList));

        //Page mapper
        Page<ProductMain> output = new PageImpl<ProductMain>(newArrayList,pageable,result.getTotalElements());
        
        return output;

    }

    public Page<ProductFeatureConfig> getFeaturedProductWithLocationParentCategory(List<String> status,String regionCountryId,String parentCategoryId, String cityId, String cityName, String name,int page, int pageSize){

        //Handling null value in order to use query
        if (regionCountryId == null || regionCountryId.isEmpty()) {
            regionCountryId = "";
        }

        if (cityId == null || cityId.isEmpty()) {
            cityId = "";
        }

        if (cityName == null || cityName.isEmpty()) {
            cityName = "";
        }

        if (name == null || name.isEmpty()) {
            name = "";
        }

        if (parentCategoryId == null || parentCategoryId.isEmpty()) {
            parentCategoryId = "";
        }

        if (status == null) {

            List<String> statusList = new ArrayList<>();
            statusList.add("ACTIVE");
            statusList.add("INACTIVE");
            statusList.add("OUTOFSTOCK");

            status = statusList;
        }

        Pageable pageable = PageRequest.of(page, pageSize);

        //get reqion country for store
        RegionCountry regionCountry = null;
        Optional<RegionCountry> optRegion = regionCountriesRepository.findById(regionCountryId);
        if (optRegion.isPresent()) {
            regionCountry = optRegion.get();
        }

        //find the based on location with pageable
        Page<ProductFeatureConfig> result = productFeaturedRepository.getQueryProductConfig(status,regionCountryId,parentCategoryId,cityId,cityName,name,pageable);

        //extract the result of content of pageable in order to proceed with dicount of item 
        // List<ProductFeatureConfig> productList = result.getContent();

        // to get discount of product
        // ProductFeatureConfig[] productWithDetailsList = GetDiscount.getProductDiscountList(productList, regionCountry, storeDiscountRepository, storeDiscountProductRepository);

        // convert array to array list
        // List<ProductFeatureConfig> newArrayList = new ArrayList<>(Arrays.asList(productWithDetailsList));

        //Page mapper
        // Page<ProductFeatureConfig> output = new PageImpl<ProductFeatureConfig>(newArrayList,pageable,result.getTotalElements());
        
        return result;
    }
}
