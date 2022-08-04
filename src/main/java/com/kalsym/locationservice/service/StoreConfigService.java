package com.kalsym.locationservice.service;

import com.kalsym.locationservice.model.RegionCity;
import com.kalsym.locationservice.model.RegionCountry;
import com.kalsym.locationservice.model.Store;
import com.kalsym.locationservice.model.StoreAssets;
import com.kalsym.locationservice.model.StoreCategory;
import com.kalsym.locationservice.model.StoreSnooze;
import com.kalsym.locationservice.model.Config.LocationConfig;
import com.kalsym.locationservice.model.Config.StoreConfig;
import com.kalsym.locationservice.repository.CategoriesSearchSpecs;
import com.kalsym.locationservice.repository.LocationConfigRepository;
import com.kalsym.locationservice.repository.RegionCountriesRepository;
import com.kalsym.locationservice.repository.StoreConfigRepository;
import com.kalsym.locationservice.utility.DateTimeUtil;
import com.kalsym.locationservice.utility.Location;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.hibernate.spatial.predicate.SpatialPredicates;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatcher;
import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;


@Service
public class StoreConfigService {
    
    @Autowired
    StoreConfigRepository storeConfigRepository;

    @Autowired
    RegionCountriesRepository regionCountriesRepository;

    @Value("${asset.service.url}")
    private String assetServiceUrl;
    
    //Get By Query USING EXAMPLE MATCHER for 
    public Page<StoreConfig> getQueryStoreConfig(String regionCountryId, String cityId, String cityName, String parentCategoryId, int page, int pageSize, String sortByCol, Sort.Direction sortingOrder){
       
        RegionCity regionCityMatch = new RegionCity();
        regionCityMatch.setName(cityName);

        StoreCategory categoryMatch = new StoreCategory();
        categoryMatch.setParentCategoryId(parentCategoryId);
    
        Store storeMatch = new Store();
        storeMatch.setRegionCountryId(regionCountryId);
        storeMatch.setCity(cityId);
        storeMatch.setRegionCityDetails(regionCityMatch);

        StoreConfig storeConfigMatch = new StoreConfig();
        storeConfigMatch.setStoreDetails(storeMatch);

        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withMatcher("regionCountryId", new GenericPropertyMatcher().exact())
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        Example<StoreConfig> example = Example.of(storeConfigMatch, matcher);

        // Specification<StoreConfig> categoriesSpec = CategoriesSearchSpecs.getSpecWithDatesBetween(parentCategoryId, example );


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

        return storeConfigRepository.findAll(example,pageable);

    }

    public Page<StoreConfig> getRawQueryStoreConfig(String regionCountryId, List<String> cityId, String cityName, String parentCategoryId,String latitude,String longitude,double searchRadius,Boolean isMainLevel,int page, int pageSize, String sortByCol, Sort.Direction sortingOrder){
    
        StoreConfig storeConfigMatch = new StoreConfig();

        Pageable pageable =null;

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
        Example<StoreConfig> example = Example.of(storeConfigMatch, matcher);

        Specification<StoreConfig> storeConfigSpecs = searchStoreConfigSpecs(regionCountryId,cityId, cityName, parentCategoryId,latitude,longitude,searchRadius,isMainLevel,example);
        Page<StoreConfig> result = storeConfigRepository.findAll(storeConfigSpecs, pageable);  

        //find the based on location with pageable
        // Page<StoreConfig> result = cityId == null?storeConfigRepository.getQueryStoreConfigRaw(cityName,regionCountryId,parentCategoryId,pageable)
        //                                         :storeConfigRepository.getQueryStoreConfigRawWithCityId(cityId, cityName, regionCountryId, parentCategoryId, pageable) ;
     
        for(StoreConfig sc : result){

            Store s = sc.getStoreDetails();
            if (latitude!=null && longitude!=null && s.getLatitude()!=null && s.getLongitude()!=null) {
                //set store distance
                double storeLat = Double.parseDouble(s.getLatitude());
                double storeLong = Double.parseDouble(s.getLongitude());
                double distance = Location.distance(Double.parseDouble(latitude), storeLat, Double.parseDouble(longitude), storeLong, 0.00, 0.00);
                s.setDistanceInMeter(distance);
            } else {
                s.setDistanceInMeter(0.00);
            }
            
            StoreSnooze st = new StoreSnooze();

            if (sc.getStoreDetails().getSnoozeStartTime()!=null && sc.getStoreDetails().getSnoozeEndTime()!=null) {
                int resultSnooze = sc.getStoreDetails().getSnoozeEndTime().compareTo(Calendar.getInstance().getTime());
                if (resultSnooze < 0) {
                    sc.getStoreDetails().setIsSnooze(false);

                    st.snoozeStartTime = null;
                    st.snoozeEndTime = null;
                    st.isSnooze = false;
                    st.snoozeReason = null;
                    sc.getStoreDetails().setStoreSnooze(st);

                } else {
            
                    sc.getStoreDetails().setIsSnooze(true);

                    Optional<RegionCountry> t = regionCountriesRepository.findById(sc.getStoreDetails().getRegionCountryId());

                    if(t.isPresent()){
                        LocalDateTime startTime = DateTimeUtil.convertToLocalDateTimeViaInstant(sc.getStoreDetails().getSnoozeStartTime(), ZoneId.of(t.get().getTimezone()));
                        LocalDateTime endTime = DateTimeUtil.convertToLocalDateTimeViaInstant(sc.getStoreDetails().getSnoozeEndTime(), ZoneId.of(t.get().getTimezone()));
                        st.snoozeStartTime = startTime;
                        st.snoozeEndTime = endTime;
                        st.isSnooze = true;
                        st.snoozeReason = sc.getStoreDetails().getSnoozeReason();

                        sc.getStoreDetails().setStoreSnooze(st);
                    }
         
                }
            } else {
                sc.getStoreDetails().setIsSnooze(false);

                st.snoozeStartTime = null;
                st.snoozeEndTime = null;
                st.isSnooze = false;
                st.snoozeReason = null;
                sc.getStoreDetails().setStoreSnooze(st);
            }

            // List<StoreAssets> listOfStoreAssets = new ArrayList<>();

            // for(StoreAssets sa:sc.getStoreDetails().getStoreAssets()){

            //     if(sa.getAssetUrl() != null){
            //         sa.setAssetUrl(assetServiceUrl+sa.getAssetUrl());

            //     } else{
            //         sa.setAssetUrl(null);

            //     }
            //     listOfStoreAssets.add(sa);
            // }
        
            // Store store = new Store();
            // store.setStoreAssets(listOfStoreAssets);

        }

        return result;
    }

    public static Specification<StoreConfig> searchStoreConfigSpecs(
        String regionCountryId, List<String> cityIdList, String cityName, String parentCategoryId, String latitude, 
        String longitude,
        double radius,
        Boolean isMainLevel,
        Example<StoreConfig> example) {

        return (Specification<StoreConfig>) (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            Join<StoreConfig, StoreCategory> storeCategory = root.join("storeCategory");
            Join<StoreConfig, Store> storeDetails = root.join("storeDetails");
            Join<Store,RegionCity> storeRegionCity = storeDetails.join("regionCityDetails");

            if (regionCountryId != null && !regionCountryId.isEmpty()) {
                predicates.add(builder.equal(storeDetails.get("regionCountryId"), regionCountryId));
            }

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


            if (parentCategoryId != null && !parentCategoryId.isEmpty()) {                
                predicates.add(builder.equal(storeCategory.get("parentCategoryId"), parentCategoryId));
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

            if (isMainLevel != null) {
                predicates.add(builder.equal(root.get("isMainLevel"), isMainLevel));
            }

            //use this if you want to group
            // query.groupBy(root.get("id"));
            query.distinct(true);
                    
            predicates.add(QueryByExamplePredicateBuilder.getPredicate(root, builder, example));

            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }



}
