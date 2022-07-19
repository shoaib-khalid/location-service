package com.kalsym.locationservice.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.kalsym.locationservice.enums.DiscountCalculationType;
import com.kalsym.locationservice.model.CustomerActivitiesSummary;
import com.kalsym.locationservice.model.RegionCountry;
import com.kalsym.locationservice.model.Store;
import com.kalsym.locationservice.model.StoreSnooze;
import com.kalsym.locationservice.model.Config.ProductFeatureConfig;
import com.kalsym.locationservice.model.Discount.StoreDiscountProduct;
import com.kalsym.locationservice.model.Product.ItemDiscount;
import com.kalsym.locationservice.model.Product.ProductInventoryWithDetails;
import com.kalsym.locationservice.model.Product.ProductMain;
import com.kalsym.locationservice.repository.CustomerActivitiesSummaryRepository;
import com.kalsym.locationservice.repository.GetDiscount;
import com.kalsym.locationservice.repository.ProductFeaturedRepository;
import com.kalsym.locationservice.repository.ProductRepository;
import com.kalsym.locationservice.repository.RegionCountriesRepository;
import com.kalsym.locationservice.repository.StoreDiscountProductRepository;
import com.kalsym.locationservice.repository.StoreDiscountRepository;
import com.kalsym.locationservice.utility.DateTimeUtil;
import com.kalsym.locationservice.utility.ProductDiscount;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    CustomerActivitiesSummaryRepository customerActivitiesSummaryRepository;

    @Value("${asset.service.url}")
    private String assetServiceUrl;

    public Page<ProductMain> getQueryProductByParentCategoryIdAndLocation(List<String> status,String regionCountryId,String parentCategoryId, List<String> cityId, String cityName, String name,int page, int pageSize){

        //Handling null value in order to use query
        if (regionCountryId == null || regionCountryId.isEmpty()) {
            regionCountryId = "";
        }

        // if (cityId == null || cityId.isEmpty()) {
        //     cityId = "";
        // }

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
        Page<ProductMain> result = cityId == null? productRepository.getProductByParentCategoryIdAndLocation(status,regionCountryId,parentCategoryId,cityName,name,pageable)
                                                : productRepository.getProductByParentCategoryIdAndLocationWithCityId(status,regionCountryId,parentCategoryId,cityId,cityName,name,pageable) ;

        //extract the result of content of pageable in order to proceed with dicount of item 
        List<ProductMain> productList = result.getContent();

        // to get discount of product
        ProductMain[] productWithDetailsList = GetDiscount.getProductDiscountList(productList, regionCountry, storeDiscountRepository, storeDiscountProductRepository);

        // convert array to array list
        List<ProductMain> newArrayList = new ArrayList<>(Arrays.asList(productWithDetailsList));

        //Page mapper
        Page<ProductMain> output = new PageImpl<ProductMain>(newArrayList,pageable,result.getTotalElements());

        //to set snooze
        for(ProductMain p : output){

            StoreSnooze st = new StoreSnooze();

            if (p.getStoreDetails().getSnoozeStartTime()!=null && p.getStoreDetails().getSnoozeEndTime()!=null) {
                int resultSnooze = p.getStoreDetails().getSnoozeEndTime().compareTo(Calendar.getInstance().getTime());
                if (resultSnooze < 0) {
                    p.getStoreDetails().setIsSnooze(false);

                    st.snoozeStartTime = null;
                    st.snoozeEndTime = null;
                    st.isSnooze = false;
                    st.snoozeReason = null;
                    p.getStoreDetails().setStoreSnooze(st);

                } else {
            
                    p.getStoreDetails().setIsSnooze(true);

                    Optional<RegionCountry> t = regionCountriesRepository.findById(p.getStoreDetails().getRegionCountryId());

                    if(t.isPresent()){
                        LocalDateTime startTime = DateTimeUtil.convertToLocalDateTimeViaInstant(p.getStoreDetails().getSnoozeStartTime(), ZoneId.of(t.get().getTimezone()));
                        LocalDateTime endTime = DateTimeUtil.convertToLocalDateTimeViaInstant(p.getStoreDetails().getSnoozeEndTime(), ZoneId.of(t.get().getTimezone()));
                        
                        st.snoozeStartTime = startTime;
                        st.snoozeEndTime = endTime;
                        st.isSnooze = true;
                        st.snoozeReason = p.getStoreDetails().getSnoozeReason();

                        p.getStoreDetails().setStoreSnooze(st);
                    }
         
                }
            } else {
                p.getStoreDetails().setIsSnooze(false);

                
                st.snoozeStartTime = null;
                st.snoozeEndTime = null;
                st.isSnooze = false;
                st.snoozeReason = null;
                p.getStoreDetails().setStoreSnooze(st);

            }
            
            //to concat with assetervice url
            //handle null
            // if(p.getThumbnailUrl() != null){
            //     p.setThumbnailUrl(assetServiceUrl+p.getThumbnailUrl());

            // } else {
            //     p.setThumbnailUrl(null);

            // }
        }
        
        return output;

    }

    /**
     * @param status
     * @param regionCountryId
     * @param parentCategoryId
     * @param cityId
     * @param cityName
     * @param name
     * @param page
     * @param pageSize
     * @param sortByCol
     * @param sortingOrder
     * @return
     */
    public Page<ProductFeatureConfig> getFeaturedProductWithLocationParentCategory(List<String> status,String regionCountryId,String parentCategoryId, List<String> cityId, String cityName, String name, Boolean isMainLevel,int page, int pageSize, String sortByCol, Sort.Direction sortingOrder){

        //Handling null value in order to use query
        if (regionCountryId == null || regionCountryId.isEmpty()) {
            regionCountryId = "";
        }

        // if (cityId == null || cityId.isEmpty()) {
        //     cityId = "";
        // }

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

        if (isMainLevel == null) {

            isMainLevel = true;
        }

        Pageable pageable;

        if (sortingOrder==Sort.Direction.DESC){
            pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).descending());
        }
        else if (sortingOrder==Sort.Direction.ASC){
            pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).ascending());
        }
        else{
            pageable = PageRequest.of(page, pageSize);
        }
        

        //get reqion country for store
        RegionCountry regionCountry = null;
        Optional<RegionCountry> optRegion = regionCountriesRepository.findById(regionCountryId);
        if (optRegion.isPresent()) {
            regionCountry = optRegion.get();
        }

        //find the based on location with pageable
        Page<ProductFeatureConfig> result = cityId == null?productFeaturedRepository.getQueryProductConfig(status,regionCountryId,parentCategoryId,cityName,name,isMainLevel,pageable)
        :productFeaturedRepository.getQueryProductConfigWithCityId(status,regionCountryId,parentCategoryId,cityId,cityName,name,isMainLevel,pageable) ;

        //extract the result of content of pageable in order to proceed with dicount of item 
        List<ProductFeatureConfig> productFeaturedList = result.getContent();

        //In order to continue get item discount , extract productDetails first
        List<ProductMain> productList = productFeaturedList.stream()
        .map(m -> {
            // System.out.println("Checking m ::::::::::::::::::::::::::"+m.getProductDetails());
            ProductMain product = m.getProductDetails();
            return product;
        })
        .collect(Collectors.toList());

        // to get discount of product
        ProductMain[] productWithDetailsList = GetDiscount.getProductDiscountList(productList, regionCountry, storeDiscountRepository, storeDiscountProductRepository);

        // convert array to array list
        List<ProductMain> newProductDetails = new ArrayList<>(Arrays.asList(productWithDetailsList));

        List<ProductFeatureConfig> listofPFC = new ArrayList<>();
        
        for (ProductFeatureConfig productfeaturedList : productFeaturedList) {

            for(ProductMain newProducWithtDetails:newProductDetails){

                if(productfeaturedList.getProductId().equals(newProducWithtDetails.getId())){

                    productfeaturedList.setProductDetails(newProducWithtDetails);

                }
            }

            listofPFC.add(productfeaturedList);
        }

        //Page mapper
        Page<ProductFeatureConfig> output = new PageImpl<ProductFeatureConfig>(listofPFC,pageable,result.getTotalElements());

        //to set store snooze
        for(ProductFeatureConfig pfc : result){

            StoreSnooze st = new StoreSnooze();

            if ( pfc.getProductDetails().getStoreDetails().getSnoozeStartTime()!=null &&  pfc.getProductDetails().getStoreDetails().getSnoozeEndTime()!=null) {
                int resultSnooze = pfc.getProductDetails().getStoreDetails().getSnoozeEndTime().compareTo(Calendar.getInstance().getTime());
                if (resultSnooze < 0) {
                    pfc.getProductDetails().getStoreDetails().setIsSnooze(false);

                    st.snoozeStartTime = null;
                    st.snoozeEndTime = null;
                    st.isSnooze = false;
                    st.snoozeReason = null;
                    pfc.getProductDetails().getStoreDetails().setStoreSnooze(st);

                } else {
            
                    pfc.getProductDetails().getStoreDetails().setIsSnooze(true);

                    Optional<RegionCountry> t = regionCountriesRepository.findById(pfc.getProductDetails().getStoreDetails().getRegionCountryId());

                    if(t.isPresent()){
                        LocalDateTime startTime = DateTimeUtil.convertToLocalDateTimeViaInstant(pfc.getProductDetails().getStoreDetails().getSnoozeStartTime(), ZoneId.of(t.get().getTimezone()));
                        LocalDateTime endTime = DateTimeUtil.convertToLocalDateTimeViaInstant(pfc.getProductDetails().getStoreDetails().getSnoozeEndTime(), ZoneId.of(t.get().getTimezone()));
                        
                        st.snoozeStartTime = startTime;
                        st.snoozeEndTime = endTime;
                        st.isSnooze = true;
                        st.snoozeReason = pfc.getProductDetails().getStoreDetails().getSnoozeReason();

                        pfc.getProductDetails().getStoreDetails().setStoreSnooze(st);
                    }
         
                }
            } else {
                pfc.getProductDetails().getStoreDetails().setIsSnooze(false);

                st.snoozeStartTime = null;
                st.snoozeEndTime = null;
                st.isSnooze = false;
                st.snoozeReason = null;
                pfc.getProductDetails().getStoreDetails().setStoreSnooze(st);
            }

            //to concat with asseteservice
            //handle null
            // if(pfc.getProductDetails().getThumbnailUrl() != null){
            //     pfc.getProductDetails().setThumbnailUrl(assetServiceUrl+pfc.getProductDetails().getThumbnailUrl());

            // } else {
            //     pfc.getProductDetails().setThumbnailUrl(null);

            // }
        }

        return output;
    }

    public List<ProductMain> getCustomerActivities(String regionCountryId){

        // List<CustomerActivitiesSummary> customerActivity = customerActivitiesSummaryRepository.findByStoreId(storeId);
        // List<CustomerActivitiesSummary> customerActivity = customerActivitiesSummaryRepository.getTrendingProducts();


        // Collection<CustomerActivitiesSummary> customerActivity = customerActivitiesSummaryRepository.getTrendingProducts(regionCountryId);
        // List<CustomerActivitiesSummary> output = new ArrayList<CustomerActivitiesSummary>(customerActivity);

        List<Object[]> vals = customerActivitiesSummaryRepository.getTrendingProductsObject(regionCountryId);

        List<String> seoNames = new ArrayList<>();

        for (Object[] row : vals) {

            // casting
            String seoName = (String)row[3].toString();
            seoNames.add(seoName);

        }

        // System.out.println("Checking seoNames ::::::::::::::::::::::::::"+seoNames);

        List<ProductMain> listOfProduct = productRepository.getProductBySeoName(seoNames,regionCountryId);
        // System.out.println("Checking listOfProduct ::::::::::::::::::::::::::"+listOfProduct);

        //get reqion country for store
        RegionCountry regionCountry = null;
        Optional<RegionCountry> optRegion = regionCountriesRepository.findById(regionCountryId);
        if (optRegion.isPresent()) {
            regionCountry = optRegion.get();
        }

        // to get discount of product
        ProductMain[] productWithDetailsList = GetDiscount.getProductDiscountList(listOfProduct, regionCountry, storeDiscountRepository, storeDiscountProductRepository);

        // convert array to array list
        List<ProductMain> newArrayList = new ArrayList<>(Arrays.asList(productWithDetailsList));

        //Page mapper
        // Page<ProductMain> output = new PageImpl<ProductMain>(newArrayList,pageable,result.getTotalElements());
        for(ProductMain p : newArrayList){

            StoreSnooze st = new StoreSnooze();

            if (p.getStoreDetails().getSnoozeStartTime()!=null && p.getStoreDetails().getSnoozeEndTime()!=null) {
                int resultSnooze = p.getStoreDetails().getSnoozeEndTime().compareTo(Calendar.getInstance().getTime());
                if (resultSnooze < 0) {
                    p.getStoreDetails().setIsSnooze(false);

                    st.snoozeStartTime = null;
                    st.snoozeEndTime = null;
                    st.isSnooze = false;
                    st.snoozeReason = null;
                    p.getStoreDetails().setStoreSnooze(st);

                } else {
            
                    p.getStoreDetails().setIsSnooze(true);

                    Optional<RegionCountry> t = regionCountriesRepository.findById(p.getStoreDetails().getRegionCountryId());

                    if(p.getStoreDetails().getStoreSnooze()== null && t.isPresent()){
                        LocalDateTime startTime = DateTimeUtil.convertToLocalDateTimeViaInstant(p.getStoreDetails().getSnoozeStartTime(), ZoneId.of(t.get().getTimezone()));
                        LocalDateTime endTime = DateTimeUtil.convertToLocalDateTimeViaInstant(p.getStoreDetails().getSnoozeEndTime(), ZoneId.of(t.get().getTimezone()));
                        st.snoozeStartTime = startTime;
                        st.snoozeEndTime = endTime;
                        st.isSnooze = true;
                        st.snoozeReason = p.getStoreDetails().getSnoozeReason();

                        p.getStoreDetails().setStoreSnooze(st);
                    }
         
                }
            } else {

                p.getStoreDetails().setIsSnooze(false);
                
                st.snoozeStartTime = null;
                st.snoozeEndTime = null;
                st.isSnooze = false;
                st.snoozeReason = null;
                p.getStoreDetails().setStoreSnooze(st);

            }        
            
            //to concat with asseteservice

            // if(p.getThumbnailUrl() != null){
            //     p.setThumbnailUrl(assetServiceUrl+p.getThumbnailUrl());

            // } else{
            //     p.setThumbnailUrl(null);

            // }
        }

        

        return newArrayList;
    }
}
