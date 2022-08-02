package com.kalsym.locationservice.service;


import com.kalsym.locationservice.LocationServiceApplication;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import com.kalsym.locationservice.model.ParentCategory;
import com.kalsym.locationservice.model.Product.ProductMain;
import com.kalsym.locationservice.model.RegionCity;
import com.kalsym.locationservice.model.RegionCountry;
import com.kalsym.locationservice.model.RegionCountryState;
import com.kalsym.locationservice.model.Store;
import com.kalsym.locationservice.model.StoreAssets;
import com.kalsym.locationservice.model.StoreCategory;
import com.kalsym.locationservice.model.StoreSnooze;
import com.kalsym.locationservice.model.TagKeyword;
import com.kalsym.locationservice.model.TagStoreDetails;
// import com.kalsym.locationservice.model.CategoryLocation;
// import com.kalsym.locationservice.model.LocationCategory;
// import com.kalsym.locationservice.repository.CategoryLocationRepository;
// import com.kalsym.locationservice.repository.LocationCategoryRepository;
import com.kalsym.locationservice.repository.StoreCategoryRepository;
import com.kalsym.locationservice.repository.ParentCategoryRepository;
import com.kalsym.locationservice.repository.RegionCountriesRepository;
import com.kalsym.locationservice.utility.DateTimeUtil;
import com.kalsym.locationservice.utility.Location;
import com.kalsym.locationservice.utility.Logger;
import java.util.Arrays;
import java.util.Collections;

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
import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;


import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;
import javax.persistence.criteria.Expression;
import org.hibernate.spatial.predicate.SpatialPredicates;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.PageImpl;

@Service
public class CategoryLocationService {

    @Autowired
    StoreCategoryRepository categoryRepository;

    @Value("${asset.service.url}")
    private String assetServiceUrl;

    @Autowired
    ParentCategoryRepository parentCategoryRepository;

    @Autowired
    RegionCountriesRepository regionCountriesRepository;
    
    //Get By Query WITH Pagination
    //Child category 
    public Page<StoreCategory> getQueryChildCategory(String cityId, String stateId,String regionCountryId, String postcode, String parentCategoryId, String sortByCol, Sort.Direction sortingOrder,int page, int pageSize){
    
        Store storeMatch = new Store();
        storeMatch.setCity(cityId);
        storeMatch.setState(stateId);
        storeMatch.setRegionCountryId(regionCountryId);
        storeMatch.setPostcode(postcode);


        ParentCategory parentCategoryMatch = new ParentCategory();
        parentCategoryMatch.setParentId(parentCategoryId);
       
        StoreCategory categoryMatch = new StoreCategory();
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
        Example<StoreCategory> example = Example.of(categoryMatch, matcher);

        Pageable pageable;
   
        if (sortingOrder==Sort.Direction.DESC){
            pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).descending());
        }
        else{
            pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).ascending());
        }
        
        Page<StoreCategory> result = categoryRepository.findAll(example,pageable);

        //to concat store asset url for response data 
        // for (Category c : result){

        //     System.out.println("Checking"+c.getStoreId());
        //     //Since we join table one to one relation : this will avoid replicate string of storeassetservice url
        //     // ParentCategory pc = new ParentCategory();
        //     // pc.setParentId(c.getParentCategory().getParentId());
        //     // pc.setParentName(c.getParentCategory().getParentName());
        //     // pc.setVerticalCode(c.getParentCategory().getVerticalCode());
        //     // pc.setDisplaySequence(c.getParentCategory().getDisplaySequence());
        //     //handle null
        //     // if(c.getParentCategory().getParentThumbnailUrl() != null){
        //     //     pc.setParentThumbnailUrl(assetServiceUrl+c.getParentCategory().getParentThumbnailUrl());

        //     // } else{
        //     //     pc.setParentThumbnailUrl(null);

        //     // }

        //     // c.setParentCategory(pc);

        //     // List<StoreAssets>  listOfAssets = new ArrayList<>();

        //     // for(StoreAssets sa:c.getStoreDetails().getStoreAssets()){

        //     //     //handle null
        //     //     if(sa.getAssetUrl() != null){
        //     //         sa.setAssetUrl(assetServiceUrl+sa.getAssetUrl());

        //     //     } else{
        //     //         sa.setAssetUrl(null);

        //     //     }

        //     //     listOfAssets.add(sa);
        //     // }
        
        //     // Store store = new Store();
        //     // store.setStoreAssets(listOfAssets);

        // }

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

    public Page<StoreCategory> getQueryStore(List<String> cityId, String cityName, String stateId,
            String regionCountryId, String postcode, String parentCategoryId, 
            String storeName,String tagKeyword, int page, int pageSize,
            String latitude, String longitude, double searchRadius, String sortByCol, Sort.Direction sortingOrder){
    
        StoreCategory storeCategoryMatch = new StoreCategory();
  
        Pageable pageable = PageRequest.of(page, pageSize);
        
        if (!sortByCol.equalsIgnoreCase("distanceInMeter")) {
            if (sortingOrder==Sort.Direction.ASC)
                pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).ascending());
            else if (sortingOrder==Sort.Direction.DESC)
                pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).descending());
        } else {
            pageable = PageRequest.of(page, pageSize);
        }
        
        ExampleMatcher matcher = ExampleMatcher
        .matchingAll()
        .withIgnoreCase()
        .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
        Example<StoreCategory> example = Example.of(storeCategoryMatch, matcher);

        Specification<StoreCategory> storeCategorySpecs = searchStoreCategorySpecs(cityId, cityName, stateId, regionCountryId,  postcode, parentCategoryId, storeName,tagKeyword,latitude,longitude,searchRadius,example);
        Page<StoreCategory> result = categoryRepository.findAll(storeCategorySpecs, pageable);       
        
        List<StoreCategory> tempStoreList = result.getContent(); 
        List<StoreCategory> newArrayList = new ArrayList<>(tempStoreList);
        
        //set store distance
        if (sortByCol.equalsIgnoreCase("distanceInMeter") && newArrayList.size()>0) {
            for (int i=0;i<newArrayList.size();i++) {
                Store s = newArrayList.get(i).getStoreDetails();
                if (latitude!=null && longitude!=null && s.getLatitude()!=null && s.getLongitude()!=null) {
                    //set store distance
                    double storeLat = Double.parseDouble(s.getLatitude());
                    double storeLong = Double.parseDouble(s.getLongitude());
                    double distance = Location.distance(Double.parseDouble(latitude), storeLat, Double.parseDouble(longitude), storeLong, 0.00, 0.00);
                    s.setDistanceInMeter(distance);
                } else {
                    s.setDistanceInMeter(0.00);
                }
            }     
            Collections.sort(newArrayList);  
            
            String logprefix="CategoryLocationService()";
            Logger.application.info(Logger.pattern, LocationServiceApplication.VERSION, logprefix, "After Sort:");
            for (int x=0;x<newArrayList.size();x++) {
                Logger.application.info(Logger.pattern, LocationServiceApplication.VERSION, logprefix, "Product store distance:"+newArrayList.get(x).getStoreDetails().getDistanceInMeter());
            }            
        }
        
        //Page mapper
        Page<StoreCategory> output = new PageImpl<StoreCategory>(newArrayList,pageable,result.getTotalElements());
        
        for(StoreCategory c : output){

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

            // List<StoreAssets>  listOfAssets = new ArrayList<>();

            // for(StoreAssets sa:c.getStoreDetails().getStoreAssets()){

            //     //handle null
            //     if(sa.getAssetUrl() != null){
            //         sa.setAssetUrl(assetServiceUrl+sa.getAssetUrl());

            //     } else{
            //         sa.setAssetUrl(null);

            //     }
            //     listOfAssets.add(sa);
            // }
        
        }

        return output;
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

    public static Specification<StoreCategory> searchStoreCategorySpecs(
        List<String> cityIdList,
        String cityName, 
        String stateId,
        String regionCountryId,
        String postcode, 
        String parentCategoryId,
        String storeName,
        String keyword, 
        String latitude, 
        String longitude,
        double radius,
        Example<StoreCategory> example) {

        return (Specification<StoreCategory>) (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            Join<StoreCategory, ParentCategory> storeParentCategory = root.join("parentCategory");
            Join<StoreCategory, Store> storeDetails = root.join("storeDetails");
            Join<Store,RegionCity> storeRegionCity = storeDetails.join("regionCityDetails");
            Join<Store,TagStoreDetails> storeTagDetails = storeDetails.join("storeTag");
            Join<TagStoreDetails,TagKeyword> storeTagKeyword = storeTagDetails.join("tagKeyword");

            
            if (cityIdList!=null) {
                int cityCount = cityIdList.size();
                List<Predicate> cityPredicatesList = new ArrayList<>();
                for (int i=0;i<cityIdList.size();i++) {
                    Predicate predicateForCity = builder.equal(storeDetails.get("city"), cityIdList.get(i));                                        
                    cityPredicatesList.add(predicateForCity);                    
                }
                Predicate finalPredicate = builder.or(cityPredicatesList.toArray(new Predicate[cityCount]));
                predicates.add(finalPredicate);
            }

            
            if (cityName != null && !cityName.isEmpty()) {
                predicates.add(builder.equal(storeRegionCity.get("name"), cityName));
            }

            if (stateId != null && !stateId.isEmpty()) {
                predicates.add(builder.equal(storeDetails.get("state"), stateId));
            }

            if (regionCountryId != null && !regionCountryId.isEmpty()) {
                predicates.add(builder.equal(storeDetails.get("regionCountryId"), regionCountryId));
            }

            if (postcode != null && !postcode.isEmpty()) {
                predicates.add(builder.equal(storeDetails.get("postcode"), postcode));
            }

            if (parentCategoryId != null && !parentCategoryId.isEmpty()) {                
                predicates.add(builder.equal(storeParentCategory.get("parentId"), parentCategoryId));
            }     
            
            if (storeName != null && !storeName.isEmpty()) {                
                predicates.add(builder.like(storeDetails.get("name"), "%"+storeName+"%"));

            }

                    
            if (keyword != null && !keyword.isEmpty()) {                
                predicates.add(builder.equal(storeTagKeyword.get("keyword"), keyword));
            }

            if (latitude!=null && longitude!=null) {
                Expression<Point> point1 = builder.function("point", Point.class, storeDetails.get("longitude"), storeDetails.get("latitude"));
                GeometryFactory factory = new GeometryFactory();
                Point comparisonPoint = factory.createPoint(new Coordinate(Double.parseDouble(longitude), Double.parseDouble(latitude))); 
                Predicate spatialPredicates = SpatialPredicates.distanceWithin(builder, point1, comparisonPoint, radius);
                predicates.add(spatialPredicates);
                
                predicates.add(builder.isNotNull(storeDetails.get("longitude")));
                predicates.add(builder.isNotNull(storeDetails.get("latitude")));
            }
            
            //use this if you want to group
            query.groupBy(storeDetails.get("id"));
                    
            predicates.add(QueryByExamplePredicateBuilder.getPredicate(root, builder, example));

            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

     
}
