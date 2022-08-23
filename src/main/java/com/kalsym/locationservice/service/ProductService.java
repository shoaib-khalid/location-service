package com.kalsym.locationservice.service;

import com.kalsym.locationservice.LocationServiceApplication;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.criteria.Expression;

import com.kalsym.locationservice.enums.DiscountCalculationType;
import com.kalsym.locationservice.model.CustomerActivitiesSummary;
import com.kalsym.locationservice.model.RegionCountry;
import com.kalsym.locationservice.model.Store;
import com.kalsym.locationservice.model.StoreSnooze;
import com.kalsym.locationservice.model.TagKeyword;
import com.kalsym.locationservice.model.TagStoreDetails;
import com.kalsym.locationservice.model.Config.ProductFeatureConfig;
import com.kalsym.locationservice.model.Config.ProductFeatureSimple;
import com.kalsym.locationservice.model.Discount.StoreDiscountProduct;
import com.kalsym.locationservice.model.Product.ItemDiscount;
import com.kalsym.locationservice.model.Product.ProductInventoryWithDetails;
import com.kalsym.locationservice.model.Product.ProductMain;
import com.kalsym.locationservice.model.RegionCity;
import com.kalsym.locationservice.model.Category;
import com.kalsym.locationservice.repository.CustomerActivitiesSummaryRepository;
import com.kalsym.locationservice.repository.GetDiscount;
import com.kalsym.locationservice.repository.ProductFeaturedRepository;
import com.kalsym.locationservice.repository.ProductRepository;
import com.kalsym.locationservice.repository.RegionCountriesRepository;
import com.kalsym.locationservice.repository.StoreDiscountProductRepository;
import com.kalsym.locationservice.repository.StoreDiscountRepository;
import com.kalsym.locationservice.utility.DateTimeUtil;
import com.kalsym.locationservice.utility.Location;
import com.kalsym.locationservice.utility.Logger;
import com.kalsym.locationservice.utility.ProductDiscount;
import java.util.Collections;
import java.util.Date;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaBuilder;

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
import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

//import com.vividsolutions.jts.geom.Coordinate;
//import com.vividsolutions.jts.geom.Geometry;
//import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Geometry;

/*import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.Point;*/
import org.hibernate.spatial.predicate.SpatialPredicates;

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

    public Page<ProductMain> getQueryProductByParentCategoryIdAndLocation(
            List<String> status,String regionCountryId,String parentCategoryId, 
            List<String> cityId, String cityName, String name, 
            String latitude, String longitude, double radius,String storeTagKeyword, Boolean isMainLevel,
            int page, int pageSize, String sortByCol, Sort.Direction sortingOrder){           

        //get reqion country for store
        RegionCountry regionCountry = null;
        Optional<RegionCountry> optRegion = regionCountriesRepository.findById(regionCountryId);
        if (optRegion.isPresent()) {
            regionCountry = optRegion.get();
        }
        
        ProductMain productMatch = new ProductMain();
      
        Pageable pageable = PageRequest.of(page, pageSize);
        
        // if (!sortByCol.equalsIgnoreCase("distanceInMeter")) {
        //     if (sortingOrder==Sort.Direction.ASC)
        //         // pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).ascending());
        //         pageable = PageRequest.of(page, pageSize);

        //     else if (sortingOrder==Sort.Direction.DESC)
        //         // pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).descending());
        //         pageable = PageRequest.of(page, pageSize);

        // } else {
        //     pageable = PageRequest.of(page, pageSize);
        // }
        
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
        Example<ProductMain> example = Example.of(productMatch, matcher);
        
        Specification productSpecs = searchProductSpecs(status, regionCountryId, parentCategoryId, cityId, cityName, name, latitude, longitude, radius,storeTagKeyword, isMainLevel, sortByCol,sortingOrder,example);
        
        Page<ProductMain> result = productRepository.findAll(productSpecs, pageable);       
        
        Logger.application.info(Logger.pattern, LocationServiceApplication.VERSION, "", "getQueryProductByParentCategoryIdAndLocation() result : "+result.toString());
        
        //extract the result of content of pageable in order to proceed with dicount of item 
        List<ProductMain> productList = result.getContent();                
        
        // to get discount of product
        ProductMain[] productWithDetailsList = GetDiscount.getProductDiscountList(productList, regionCountry, storeDiscountRepository, storeDiscountProductRepository);

        // convert array to array list
        List<ProductMain> newArrayList = new ArrayList<>(Arrays.asList(productWithDetailsList));
        
        //set store distance
        // if (sortByCol.equalsIgnoreCase("distanceInMeter") && newArrayList.size()>0) {
        //     for (int i=0;i<newArrayList.size();i++) {
        //         Store s = newArrayList.get(i).getStoreDetails();
        //         if (latitude!=null && longitude!=null && s.getLatitude()!=null && s.getLongitude()!=null) {
        //             //set store distance
        //             double storeLat = Double.parseDouble(s.getLatitude());
        //             double storeLong = Double.parseDouble(s.getLongitude());
        //             double distance = Location.distance(Double.parseDouble(latitude), storeLat, Double.parseDouble(longitude), storeLong, 0.00, 0.00);
        //             s.setDistanceInMeter(distance);
        //         } else {
        //             s.setDistanceInMeter(0.00);
        //         }
        //     }     
        //     Collections.sort(newArrayList);  
            
        //     String logprefix="ProductService()";
        //     Logger.application.info(Logger.pattern, LocationServiceApplication.VERSION, logprefix, "After Sort:");
        //     for (int x=0;x<newArrayList.size();x++) {
        //         Logger.application.info(Logger.pattern, LocationServiceApplication.VERSION, logprefix, "Product store distance:"+newArrayList.get(x).getStoreDetails().getDistanceInMeter());
        //     }
        // }
        
        //Page mapper
        Page<ProductMain> output = new PageImpl<ProductMain>(newArrayList,pageable,result.getTotalElements());

        //to set snooze
        for(ProductMain p : output){

            //set store distance
            Store s = p.getStoreDetails();
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
    public Page<ProductFeatureConfig> getFeaturedProductWithLocationParentCategory(List<String> status,String regionCountryId,String parentCategoryId, List<String> cityId, String cityName, String name, Boolean isMainLevel,String latitude,String longitude,double searchRadius,int page, int pageSize, String sortByCol, Sort.Direction sortingOrder){


        ProductFeatureConfig productFeatureConfigMatch = new ProductFeatureConfig();

        Pageable pageable =PageRequest.of(page, pageSize);

        ExampleMatcher matcher = ExampleMatcher
        .matchingAll()
        .withIgnoreCase()
        .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
        Example<ProductFeatureConfig> example = Example.of(productFeatureConfigMatch, matcher);
        
        //get reqion country for store
        RegionCountry regionCountry = null;
        Optional<RegionCountry> optRegion = regionCountriesRepository.findById(regionCountryId);
        if (optRegion.isPresent()) {
            regionCountry = optRegion.get();
        }

        
        Specification<ProductFeatureConfig> productFeatureConfigSpecs = searchProductFeatureConfigSpecs(status,regionCountryId,parentCategoryId,cityId,cityName,name,isMainLevel,latitude,longitude,searchRadius,example);
        Page<ProductFeatureConfig> result = productFeaturedRepository.findAll(productFeatureConfigSpecs, pageable);   

        // Page<ProductFeatureConfig> result;
        // if(isMainLevel != null){
        //     //find the based on location with pageable
        //     result = cityId == null?productFeaturedRepository.getQueryProductConfig(status,regionCountryId,parentCategoryId,cityName,name,isMainLevel,pageable)
        //     :productFeaturedRepository.getQueryProductConfigWithCityId(status,regionCountryId,parentCategoryId,cityId,cityName,name,isMainLevel,pageable) ;

        // } else{
        //     //find the based on location with pageable
        //     result = cityId == null?productFeaturedRepository.getAllQueryProductConfig(status,regionCountryId,parentCategoryId,cityName,name,pageable)
        //     :productFeaturedRepository.getAllQueryProductConfigWithCityId(status,regionCountryId,parentCategoryId,cityId,cityName,name,pageable) ;
        // }

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

            Store s = pfc.getProductDetails().getStoreDetails();
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
    
    
    public static Specification<ProductMain> searchProductSpecs(
            List<String> statusList, 
            String regionCountryId, 
            String parentCategoryId, 
            List<String> cityIdList, 
            String cityName, 
            String productName, 
            String latitude, 
            String longitude,
            double radius,
            String storeTagKeyword,
            Boolean isMainLevel,
            String sortByCol, Sort.Direction sortingOrder,
            Example<ProductMain> example) {

        return (Specification<ProductMain>) (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            Join<ProductMain, Store> store = root.join("storeDetails");
            Join<ProductMain, Category> storeCategory = root.join("storeCategory");
            Join<Store, RegionCity> regionCity = store.join("regionCityDetails");
            Join<Store,TagStoreDetails> storeTagDetails = store.join("storeTag", JoinType.LEFT);
            Join<TagStoreDetails,TagKeyword> storeTagKeywords = storeTagDetails.join("tagKeyword", JoinType.LEFT);
            Join<ProductMain,ProductFeatureSimple> productFeatured = root.join("featuredProduct", JoinType.LEFT);
            
            if (regionCountryId != null && !regionCountryId.isEmpty()) {
                predicates.add(builder.equal(store.get("regionCountryId"), regionCountryId));
            }

            if (cityName != null && !cityName.isEmpty()) {
                predicates.add(builder.equal(regionCity.get("name"), cityName));
            }

            if (productName != null && !productName.isEmpty()) {
                predicates.add(builder.like(root.get("name"), "%"+productName+"%"));
            }

            if (parentCategoryId != null && !parentCategoryId.isEmpty()) {                
                predicates.add(builder.equal(storeCategory.get("parentCategoryId"), parentCategoryId));
            }          
            
            if (statusList!=null) {
                int statusCount = statusList.size();
                List<Predicate> statusPredicatesList = new ArrayList<>();
                for (int i=0;i<statusList.size();i++) {
                    Predicate predicateForProductStatus = builder.equal(root.get("status"), statusList.get(i));                                        
                    statusPredicatesList.add(predicateForProductStatus);                    
                }
                Predicate finalPredicate = builder.or(statusPredicatesList.toArray(new Predicate[statusCount]));
                predicates.add(finalPredicate);
            }

            if (storeTagKeyword != null && !storeTagKeyword.isEmpty()) {                
                predicates.add(builder.equal(storeTagKeywords.get("keyword"), storeTagKeyword));
            }

            
            if (cityIdList!=null) {
                int cityCount = cityIdList.size();
                List<Predicate> cityPredicatesList = new ArrayList<>();
                for (int i=0;i<cityIdList.size();i++) {
                    Predicate predicateForCity = builder.equal(store.get("city"), cityIdList.get(i));                                        
                    cityPredicatesList.add(predicateForCity);                    
                }
                Predicate finalPredicate = builder.or(cityPredicatesList.toArray(new Predicate[cityCount]));
                predicates.add(finalPredicate);
            }
            
            if (latitude!=null && longitude!=null) {
                 //Join<ProductMain, Store> store = root.join("storeDetails");
                
                /*
                //calculate using radius 
                double radius=20000; 
                Expression<Point> point1 = builder.function("point", Point.class, store.get("longitude"), store.get("latitude"));
                GeometryFactory factory = new GeometryFactory();
                Point comparisonPoint = factory.createPoint(new Coordinate(Double.parseDouble(latitude), Double.parseDouble(longitude)));           
                Predicate spatialPredicates = SpatialPredicates.distanceWithin(builder, point1, comparisonPoint, radius);
                predicates.add(spatialPredicates);
                */
                
                //calculate using polygon
                //create polygon based on user coordinate                
                /*String polygonPoint = "polygon((101.427 3.107 , 101.593 3.095, 101.654 2.999, 101.433 2.991, 101.427 3.107))";                
                Expression<Point> geo1 = builder.function("point", Point.class, store.get("longitude"), store.get("latitude"));
                Expression<Polygon> geo2 = builder.function("ST_GEOMFROMTEXT", Polygon.class, builder.literal(polygonPoint));                                
                Predicate spatialPredicates = SpatialPredicates.within(builder, geo1, geo2);
                predicates.add(spatialPredicates);
                */
                
                Expression<Point> point1 = builder.function("point", Point.class, store.get("longitude"), store.get("latitude"));
                GeometryFactory factory = new GeometryFactory();
                Point comparisonPoint = factory.createPoint(new Coordinate(Double.parseDouble(longitude), Double.parseDouble(latitude))); 
                Predicate spatialPredicates = SpatialPredicates.distanceWithin(builder, point1, comparisonPoint, radius);
                predicates.add(spatialPredicates);
                
                predicates.add(builder.isNotNull(store.get("longitude")));
                predicates.add(builder.isNotNull(store.get("latitude")));
            }

            // select * from product p 
            // WHERE p.name like '%black%'
            // ORDER BY 
            // (CASE WHEN p.thumbnailUrl  IS NULL THEN 1 ELSE 0 END),
            // p.name ASC

            //https://stackoverflow.com/questions/46541922/jpa-criteriaquery-order-by-with-two-criteria

            
            List<Order> orderList = new ArrayList<Order>();
            
            if (isMainLevel!=null && isMainLevel==true) {
               //sORT FEATURED FIRST , THEN IMAGE
                orderList.add(builder.asc(builder.selectCase()
                .when(productFeatured.get("id").isNull(), 1)
                .otherwise(0)));
                
                orderList.add(builder.desc(productFeatured.get("isMainLevel")));
                
                orderList.add(builder.asc(productFeatured.get("mainLevelSequence")));
            } else {
                //sORT FEATURED FIRST , THEN IMAGE
                orderList.add(builder.asc(builder.selectCase()
                .when(productFeatured.get("id").isNull(), 1)
                .otherwise(0)));

                orderList.add(builder.asc(productFeatured.get("sequence")));              
            }
            
            //sORT IMAGE FIRST , THEN PRODUCT SORT by <COLUMN>
              orderList.add
              (builder.asc(builder.selectCase()
              .when(root.get("thumbnailUrl").isNull(), 1)
              .otherwise(0)));
            
            //sort by verticalCode, FNB front
            orderList.add(builder.desc(store.get("verticalCode")));  
              
            if (sortingOrder==Sort.Direction.ASC){
                orderList.add(builder.asc(root.get(sortByCol)));

            }else{
                orderList.add(builder.desc(root.get(sortByCol)));

            }

            query.orderBy(orderList);
            
            predicates.add(QueryByExamplePredicateBuilder.getPredicate(root, builder, example));

            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    public static Specification<ProductFeatureConfig> searchProductFeatureConfigSpecs(
        List<String> statusList,String regionCountryId,String parentCategoryId, List<String> cityIdList, String cityName, String productName, Boolean isMainLevel,
        String latitude, 
        String longitude,
        double radius,
        Example<ProductFeatureConfig> example) {

        return (Specification<ProductFeatureConfig>) (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            Join<ProductFeatureConfig, ProductMain> productDetails = root.join("productDetails");
            Join<ProductMain, Store> storeDetails = productDetails.join("storeDetails");
            Join<ProductMain, Category> storeCategory = productDetails.join("storeCategory");
            Join<Store, RegionCity> regionCityDetails = storeDetails.join("regionCityDetails");

            
            if (statusList!=null) {
                int statusCount = statusList.size();
                List<Predicate> statusPredicatesList = new ArrayList<>();
                for (int i=0;i<statusList.size();i++) {
                    Predicate predicateForProductStatus = builder.equal(productDetails.get("status"), statusList.get(i));                                        
                    statusPredicatesList.add(predicateForProductStatus);                    
                }
                Predicate finalPredicate = builder.or(statusPredicatesList.toArray(new Predicate[statusCount]));
                predicates.add(finalPredicate);
            }

                 
            if (regionCountryId != null && !regionCountryId.isEmpty()) {
                predicates.add(builder.equal(storeDetails.get("regionCountryId"), regionCountryId));
            }

            if (parentCategoryId != null && !parentCategoryId.isEmpty()) {                
                predicates.add(builder.equal(storeCategory.get("parentCategoryId"), parentCategoryId));
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
                predicates.add(builder.equal(regionCityDetails.get("name"), cityName));
            }

            if (productName != null && !productName.isEmpty()) {
                predicates.add(builder.like(productDetails.get("name"), "%"+productName+"%"));
            }

            if (isMainLevel != null) {
                predicates.add(builder.equal(root.get("isMainLevel"), isMainLevel));
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
             
            predicates.add(QueryByExamplePredicateBuilder.getPredicate(root, builder, example));

            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
    
    /*
    org.hibernate.spatial.predicate.SpatialPredicates.distanceWithin
    (javax.persistence.criteria.CriteriaBuilder,
        javax.persistence.criteria.Expression<? extends org.locationtech.jts.geom.Geometry>,
        javax.persistence.criteria.Expression<? extends org.locationtech.jts.geom.Geometry>,
        javax.persistence.criteria.Expression<java.lang.Double>) 
    */

    //=============not using this one
    public static Specification<ProductMain> filterWithinRadius(double longitude, double latitude, double radius) {
        return new Specification<ProductMain>() {
            @Override
            public Predicate toPredicate(Root<ProductMain> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                String polygonPoint = "polygon((101.427 3.107 , 101.593 3.095, 101.654 2.999, 101.433 2.991, 101.427 3.107))";                
                Join<ProductMain, Store> store = root.join("storeDetails");
                Expression<Point> geo1 = builder.function("point", Point.class, store.get("longitude"), store.get("latitude"));
                Expression<Polygon> geo2 = builder.function("ST_GEOMFROMTEXT", Polygon.class, builder.literal(polygonPoint));                                
                return SpatialPredicates.within(builder, geo1, geo2);
            }
        };
    }
    
  
    public static Specification<ProductMain> filterCalculateRange(double longitude, double latitude, double radius) {
        return new Specification<ProductMain>() {
            @Override
            public Predicate toPredicate(Root<ProductMain> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                Join<ProductMain, Store> store = root.join("storeDetails");
                Expression<Point> point1 = builder.function("point", Point.class, store.get("longitude"), store.get("latitude"));
                GeometryFactory factory = new GeometryFactory();
                Point comparisonPoint = factory.createPoint(new Coordinate(latitude, longitude));           
                return SpatialPredicates.distanceWithin(builder, point1, comparisonPoint, radius);
            }
        };
    }
}
