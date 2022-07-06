package com.kalsym.locationservice.service;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import com.kalsym.locationservice.model.Category;
import com.kalsym.locationservice.model.ParentCategory;
import com.kalsym.locationservice.model.RegionCountry;
import com.kalsym.locationservice.model.Store;
import com.kalsym.locationservice.model.StoreAssets;
import com.kalsym.locationservice.model.StoreSnooze;
// import com.kalsym.locationservice.model.CategoryLocation;
// import com.kalsym.locationservice.model.LocationCategory;
// import com.kalsym.locationservice.repository.CategoryLocationRepository;
// import com.kalsym.locationservice.repository.LocationCategoryRepository;
import com.kalsym.locationservice.repository.CategoryRepository;
import com.kalsym.locationservice.repository.ParentCategoryRepository;
import com.kalsym.locationservice.repository.RegionCountriesRepository;
import com.kalsym.locationservice.utility.DateTimeUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Example;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatcher;


import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

@Service
public class CategoryLocationService {

    @Autowired
    CategoryRepository categoryRepository;

    @Value("${asset.service.url}")
    private String assetServiceUrl;

    @Autowired
    ParentCategoryRepository parentCategoryRepository;

    @Autowired
    RegionCountriesRepository regionCountriesRepository;
    
    //Get By Query WITH Pagination
    //Child category 
    public Page<Category> getQueryChildCategory(String cityId, String stateId,String regionCountryId, String postcode, String parentCategoryId, String sortByCol, Sort.Direction sortingOrder,int page, int pageSize){
    
        Store storeMatch = new Store();
        storeMatch.setCity(cityId);
        storeMatch.setState(stateId);
        storeMatch.setRegionCountryId(regionCountryId);
        storeMatch.setPostcode(postcode);


        ParentCategory parentCategoryMatch = new ParentCategory();
        parentCategoryMatch.setParentId(parentCategoryId);
       
        Category categoryMatch = new Category();
        categoryMatch.setStoreDetails(storeMatch);
        categoryMatch.setParentCategory(parentCategoryMatch);

        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withMatcher("city", new GenericPropertyMatcher().exact())
                .withMatcher("stateId", new GenericPropertyMatcher().exact())
                .withMatcher("regionCountryId", new GenericPropertyMatcher().exact())
                .withMatcher("postcode", new GenericPropertyMatcher().exact())
                .withMatcher("parentCategoryId", new GenericPropertyMatcher().exact())
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Category> example = Example.of(categoryMatch, matcher);

        Pageable pageable;
   
        if (sortingOrder==Sort.Direction.DESC){
            pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).descending());
        }
        else{
            pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).ascending());
        }
        
        Page<Category> result = categoryRepository.findAll(example,pageable);

        //to concat store asset url for response data 
        for (Category c : result){

            System.out.println("Checking"+c.getStoreId());
            //Since we join table one to one relation : this will avoid replicate string of storeassetservice url
            // ParentCategory pc = new ParentCategory();
            // pc.setParentId(c.getParentCategory().getParentId());
            // pc.setParentName(c.getParentCategory().getParentName());
            // pc.setVerticalCode(c.getParentCategory().getVerticalCode());
            // pc.setDisplaySequence(c.getParentCategory().getDisplaySequence());
            //handle null
            // if(c.getParentCategory().getParentThumbnailUrl() != null){
            //     pc.setParentThumbnailUrl(assetServiceUrl+c.getParentCategory().getParentThumbnailUrl());

            // } else{
            //     pc.setParentThumbnailUrl(null);

            // }

            // c.setParentCategory(pc);

            List<StoreAssets>  listOfAssets = new ArrayList<>();

            for(StoreAssets sa:c.getStoreDetails().getStoreAssets()){

                //handle null
                if(sa.getAssetUrl() != null){
                    sa.setAssetUrl(assetServiceUrl+sa.getAssetUrl());

                } else{
                    sa.setAssetUrl(null);

                }

                listOfAssets.add(sa);
            }
        
            // Store store = new Store();
            // store.setStoreAssets(listOfAssets);

        }

        return result;

    }
    
    // //parent category
    // public List<Object> getQueryParentCategories(String city, String stateId, String regionCountryId, String postcode){

    //     List<Object[]> result = categoryRepository.getParentCategoriesBasedOnLocation(stateId,city,postcode,regionCountryId);
    //     // System.out.println("Checking result 1 :::"+result.toString());
    //     List<Object> parentCategoriesList = result.stream()
    //     .map(m -> {
    //         // System.out.println("Checking m[0] :::"+m[2]);
    //         ParentCategory parentCategoryList = new ParentCategory();
    //         parentCategoryList.setParentId(m[0].toString());
    //         parentCategoryList.setParentName(m[1].toString());
    //         parentCategoryList.setParentThumbnailUrl(m[2]== null?"":m[2].toString());
    //         return parentCategoryList;
    //     })
    //     .collect(Collectors.toList());

    //     return parentCategoriesList ;
    // }

    public Page<Category> getQueryStore(List<String> cityId, String cityName, String stateId,String regionCountryId, String postcode, String parentCategoryId, String storeName, int page, int pageSize){
    
        //Handling null value in order to use query
        // if (cityId == null || cityId.isEmpty()) {
        //     cityId = "";
        // }

        // if (stateId == null || stateId.isEmpty()) {
        //     stateId = "";
        // }
        if (cityName == null || cityName.isEmpty()) {
            cityName = "";
        }

        if (regionCountryId == null || regionCountryId.isEmpty()) {
            regionCountryId = "";
        }

        // if (postcode == null || postcode.isEmpty()) {
        //     postcode = "";
        // }

        if (parentCategoryId == null || parentCategoryId.isEmpty()) {
            parentCategoryId = "";
        }

        if (storeName == null || storeName.isEmpty()) {
            storeName = "";
        }

        Pageable pageable = PageRequest.of(page, pageSize);

        //find the based on location with pageable
        Page<Category> result = cityId == null? categoryRepository.getStoreBasedOnParentCategories(cityName,stateId,regionCountryId,postcode,parentCategoryId,storeName,pageable)
                                            : categoryRepository.getStoreBasedOnParentCategoriesWithCityId(cityId,cityName,stateId,regionCountryId,postcode,parentCategoryId,storeName,pageable);
   
        // if (sortingOrder==Sort.Direction.DESC){
        //     pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).descending());
        // }
        // else{
        //     pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).ascending());
        // }
        
        // return categoryRepository.findAll(example,pageable);

        for(Category c : result){

            StoreSnooze st = new StoreSnooze();

            if (c.getStoreDetails().getSnoozeStartTime()!=null && c.getStoreDetails().getSnoozeEndTime()!=null) {
                int resultSnooze = c.getStoreDetails().getSnoozeEndTime().compareTo(Calendar.getInstance().getTime());
                if (resultSnooze < 0) {
                    c.getStoreDetails().setIsSnooze(false);

                    st.snoozeStartTime = null;
                    st.snoozeEndTime = null;
                    st.isSnooze = false;
                    st.snoozeReason = null;
                    c.getStoreDetails().setStoreSnooze(st);

                } else {
            
                    c.getStoreDetails().setIsSnooze(true);

                    Optional<RegionCountry> t = regionCountriesRepository.findById(c.getStoreDetails().getRegionCountryId());

                    if(t.isPresent()){
                        LocalDateTime startTime = DateTimeUtil.convertToLocalDateTimeViaInstant(c.getStoreDetails().getSnoozeStartTime(), ZoneId.of(t.get().getTimezone()));
                        LocalDateTime endTime = DateTimeUtil.convertToLocalDateTimeViaInstant(c.getStoreDetails().getSnoozeEndTime(), ZoneId.of(t.get().getTimezone()));
                        
                        st.snoozeStartTime = startTime;
                        st.snoozeEndTime = endTime;
                        st.isSnooze = true;
                        st.snoozeReason = c.getStoreDetails().getSnoozeReason();

                        c.getStoreDetails().setStoreSnooze(st);
                    }
         
                }
            } else {
                c.getStoreDetails().setIsSnooze(false);

                st.snoozeStartTime = null;
                st.snoozeEndTime = null;
                st.isSnooze = false;
                st.snoozeReason = null;
                c.getStoreDetails().setStoreSnooze(st);
            }

            List<StoreAssets>  listOfAssets = new ArrayList<>();

            for(StoreAssets sa:c.getStoreDetails().getStoreAssets()){

                //handle null
                if(sa.getAssetUrl() != null){
                    sa.setAssetUrl(assetServiceUrl+sa.getAssetUrl());

                } else{
                    sa.setAssetUrl(null);

                }
                listOfAssets.add(sa);
            }
        
        }

        return result;
    }

    public Page<ParentCategory> getQueryParentCategoriesBasedOnLocation(List<String> cityId, String stateId, String regionCountryId, String postcode, String parentCategoryId, String sortByCol,Sort.Direction sortingOrder, int page, int pageSize){

        if (parentCategoryId == null || parentCategoryId.isEmpty()) {
            parentCategoryId = "";
        }

        Pageable pageable;

        if (sortingOrder==Sort.Direction.DESC){
            pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).descending());
        }
        else if(sortingOrder==Sort.Direction.ASC){
            pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).ascending());
        }
        else{
            pageable = PageRequest.of(page, pageSize);
        }


        //If query param only regiOn country we will display all the parent category based on vertical code
        List<String> verticalCode = new ArrayList<>();
        
        Page<ParentCategory> result;

        if(cityId == null){
            switch (regionCountryId){

                case "PAK":
    
                    verticalCode.add("FnB_PK");
                    verticalCode.add("ECommerce_PK");
    
                    result = parentCategoryRepository.getAllParentCategoriesBasedOnCountry(verticalCode,parentCategoryId,pageable);
    
                break;
    
                default:
    
                    verticalCode.add("FnB");
                    verticalCode.add("E-Commerce");
    
                    result = parentCategoryRepository.getAllParentCategoriesBasedOnCountry(verticalCode,parentCategoryId,pageable);
    
            }

        } else{
            
            result = parentCategoryRepository.getParentCategoriesBasedOnLocationWithCityIdQuery(stateId,cityId,postcode,regionCountryId,parentCategoryId,pageable);
        }
                        
        //to concat store asset url for response data
        // for (ParentCategory pc : result){
        //     //handle null
        //     if(pc.getParentThumbnailUrl() != null){
        //         pc.setParentThumbnailUrl(assetServiceUrl+pc.getParentThumbnailUrl());

        //     } else{
        //         pc.setParentThumbnailUrl(null);

        //     }
        // }

        return result;
    }

     
}
