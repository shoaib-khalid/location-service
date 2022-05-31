package com.kalsym.locationservice.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.kalsym.locationservice.enums.DiscountCalculationType;
import com.kalsym.locationservice.model.RegionCountry;
import com.kalsym.locationservice.model.Store;
import com.kalsym.locationservice.model.Discount.StoreDiscountProduct;
import com.kalsym.locationservice.model.Product.ItemDiscount;
import com.kalsym.locationservice.model.Product.ProductInventoryWithDetails;
import com.kalsym.locationservice.model.Product.ProductMain;
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

        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withMatcher("city", new GenericPropertyMatcher().exact())
                .withMatcher("stateId", new GenericPropertyMatcher().exact())
                .withMatcher("regionCountryId", new GenericPropertyMatcher().exact())
                .withMatcher("postcode", new GenericPropertyMatcher().exact())
                .withMatcher("status", new GenericPropertyMatcher().exact())
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        Example<ProductMain> example = Example.of(productMainMatch, matcher);

        Pageable pageable;
   
        if (sortingOrder==Sort.Direction.DESC){
            pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).descending());
        }
        else{
            pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).ascending());
        }
        
        return productRepository.findAll(example,pageable);

    }

    public Page<ProductMain> getRawQueryProduct(String city, String stateId,String regionCountryId, String postcode, List<String> status,String name, String sortByCol, Sort.Direction sortingOrder,int page, int pageSize){
    
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

        if (name == null || name.isEmpty()) {
            name = "";
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

        //get reqion country for store
        RegionCountry regionCountry = null;
        Optional<RegionCountry> optRegion = regionCountriesRepository.findById(regionCountryId);
        if (optRegion.isPresent()) {
            regionCountry = optRegion.get();
        }

        //find the based on location with pageable
        Page<ProductMain> result = productRepository.getProductBasedOnLocation(status,stateId,regionCountryId,city,postcode,name,pageable);

        //extract the result of content of pageable in order to proceed with dicount of item 
        List<ProductMain> productList = result.getContent();

        ProductMain[] productWithDetailsList = new ProductMain[productList.size()];

        for (int x=0;x<productList.size();x++) {

            //check for item discount in hashmap
            ProductMain productDetails = productList.get(x);
            for (int i=0;i<productDetails.getProductInventories().size();i++) {
                
                ProductInventoryWithDetails productInventory = productDetails.getProductInventories().get(i);
                String storeId = productDetails.getStoreDetails().getId();

                //ItemDiscount discountDetails = discountedItemMap.get(productInventory.getItemCode());
                /*ItemDiscount discountDetails = hashmapLoader.GetDiscountedItemMap(storeId, productInventory.getItemCode());*/
                ItemDiscount discountDetails = ProductDiscount.getItemDiscount(storeDiscountRepository, storeId, productInventory.getItemCode(), regionCountry);
                if (discountDetails != null) {                    
                    double discountedPrice = productInventory.getPrice();
                    if (discountDetails.calculationType.equals(DiscountCalculationType.FIX)) {
                        discountedPrice = productInventory.getPrice() - discountDetails.discountAmount;
                    } else if (discountDetails.calculationType.equals(DiscountCalculationType.PERCENT)) {
                        discountedPrice = productInventory.getPrice() - (discountDetails.discountAmount / 100 * productInventory.getPrice());
                    }
                    discountDetails.discountedPrice = discountedPrice;
                    discountDetails.normalPrice = productInventory.getPrice();                    
                    productInventory.setItemDiscount(discountDetails); 
                } else {
                    //get inactive discount if any
                    List<StoreDiscountProduct> discountList = storeDiscountProductRepository.findByItemCode(productInventory.getItemCode());
                    if (!discountList.isEmpty()) {
                        StoreDiscountProduct storeDiscountProduct = discountList.get(0);
                        ItemDiscount inactiveDiscount = new ItemDiscount();
                        inactiveDiscount.discountId = storeDiscountProduct.getStoreDiscountId();
                        productInventory.setItemDiscountInactive(inactiveDiscount);
                    }
                
                }
            }

            productWithDetailsList[x]=productDetails;

        }

        // convert array to array list
        List<ProductMain> newArrayList = new ArrayList<>(Arrays.asList(productWithDetailsList));

        //Page mapper
        Page<ProductMain> output = new PageImpl<ProductMain>(newArrayList,pageable,result.getTotalElements());
        
        return output;
    }
}
