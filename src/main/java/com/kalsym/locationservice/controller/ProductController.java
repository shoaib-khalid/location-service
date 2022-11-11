package com.kalsym.locationservice.controller;

import java.util.List;


import javax.servlet.http.HttpServletRequest;

import com.kalsym.locationservice.model.CustomerActivitiesSummary;
import com.kalsym.locationservice.model.Product.ProductMain;
import com.kalsym.locationservice.model.TagKeyword;
import com.kalsym.locationservice.model.TagKeywordDetails;
import com.kalsym.locationservice.model.TagStoreDetails;
import com.kalsym.locationservice.service.CategoryLocationService;
import com.kalsym.locationservice.LocationServiceApplication;
import com.kalsym.locationservice.enums.DiscountCalculationType;
import com.kalsym.locationservice.model.Config.TagConfig;
import com.kalsym.locationservice.model.Product.ItemDiscount;
import com.kalsym.locationservice.model.Product.ProductInventoryWithDetails;
import com.kalsym.locationservice.service.ProductService;
import com.kalsym.locationservice.service.TagKeywordService;
import com.kalsym.locationservice.repository.ProductRepository;
import com.kalsym.locationservice.repository.TagKeywordDetailsRepository;
import com.kalsym.locationservice.repository.StoreDiscountRepository;
import com.kalsym.locationservice.repository.StoreWithDetailsRepository;
import com.kalsym.locationservice.repository.RegionCountriesRepository;
import com.kalsym.locationservice.utility.HttpResponse;
import com.kalsym.locationservice.utility.Logger;
import com.kalsym.locationservice.utility.ProductDiscount;
import com.kalsym.locationservice.model.RegionCountry;
import com.kalsym.locationservice.model.StoreWithDetails;
import java.util.ArrayList;
import java.util.Optional;

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
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("")
public class ProductController {
    
    @Autowired
    ProductService productService;
    
    @Value("${product.search.radius}")
    private Double searchRadius;
    
    @Autowired
    ProductRepository productRepository;
    
    @Autowired
    StoreWithDetailsRepository storeRepository;
    
    @Autowired
    StoreDiscountRepository storeDiscountRepository;
    
    @Autowired
    TagKeywordDetailsRepository tagKeywordDetailsRepository;
    
    @Autowired
    RegionCountriesRepository regionCountriesRepository;
    
     @Value("${famous.max.list:30}")
    private int famousMaxList;
     
    @Value("${famous.limit.perstore:5}")
    private int famousLimitPerStore;
      
    @Value("${famous.min.order:30}")
    private int famousMinimumOrder;
       
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
    
    
    @GetMapping(path = {"/famous/{tagKeyword}"}, name = "order-items-get")
    @PreAuthorize("hasAnyAuthority('order-items-get', 'all')")
    public ResponseEntity<HttpResponse> famousItems(HttpServletRequest request,
            @PathVariable(required = true) String tagKeyword) throws Exception {
        String logprefix = request.getRequestURI() + " ";

        Logger.application.info(Logger.pattern, LocationServiceApplication.VERSION, logprefix, "product-famous, tagKeyword: " + tagKeyword);
        
        TagKeywordDetails tag = tagKeywordDetailsRepository.findByKeyword(tagKeyword);
        List<ProductMain> famousProductList = new ArrayList();
        int minimumOrder=famousMinimumOrder;
        int productLimit=famousMaxList;
        int limitPerStore=famousLimitPerStore;
        
        if (tag!=null) {                            
            String tagType="foodcourt";
            for (int i=0;i<tag.getTagConfig().size();i++) {
                TagConfig tagConfig = tag.getTagConfig().get(i);
                if (tagConfig.getProperty().equals("type")) {
                    tagType = tagConfig.getContent();
                }
            }
            
            int limit = limitPerStore;
            if (tagType.equalsIgnoreCase("restaurant")) {
               limit=productLimit;
            }

            //get famous product for the store
            for (int x=0;x<tag.getTagStoreDetails().size();x++) {
                TagStoreDetails tagDetails = tag.getTagStoreDetails().get(x);
                String storeId = tagDetails.getStoreId();
               
                if (storeId!=null) {
                    List<Object[]> productList = productRepository.getFamousItemByStoreIdSnapshot(storeId, limit, minimumOrder);
                    Logger.application.info(Logger.pattern, LocationServiceApplication.VERSION, logprefix, "Product found:"+productList.size());
                    for (int z=0;z<productList.size();z++) {
                           Object[] product = productList.get(z);
                           String productId = (String)product[2];
                           if (productId!=null) {
                                Optional<ProductMain> productInfoOpt = productRepository.findById(productId);
                                if (productInfoOpt.isPresent()) {
                                     ProductMain productInfo = productInfoOpt.get();                            
                                     famousProductList.add(productInfo);
                                }
                           }
                    }
                }
            }
        } else {
            //find in store
            String storeId = tagKeyword;
            List<Object[]> productList = productRepository.getFamousItemByStoreId(storeId, productLimit);
            
            for (int z=0;z<productList.size();z++) {
                  Object[] product = productList.get(z);
                  String productId = (String)product[2];
                  if (productId!=null) {
                    Optional<ProductMain> productInfoOpt = productRepository.findById(productId);
                    if (productInfoOpt.isPresent()) {
                        ProductMain productInfo = productInfoOpt.get();
                        famousProductList.add(productInfo);
                    }
                  }
            }
        }
        
        
        if (famousProductList.size()<productLimit) {
            //add product from tag product feature
            if (tag!=null && tag.getProductFeatureList()!=null && !tag.getProductFeatureList().isEmpty()) {
                Logger.application.info(Logger.pattern, LocationServiceApplication.VERSION, logprefix, "featured product list size:"+tag.getProductFeatureList().size());
                for (int x=0;x<tag.getProductFeatureList().size();x++) {
                    ProductMain featureProduct = tag.getProductFeatureList().get(x).getProductDetails();
                    Logger.application.info(Logger.pattern, LocationServiceApplication.VERSION, logprefix, "featured product["+x+"]:"+featureProduct);
                    if (featureProduct!=null) {
                        //check if already exist in the list
                        boolean productExist = false;
                        for (int z=0;z<famousProductList.size();z++) {
                            Logger.application.info(Logger.pattern, LocationServiceApplication.VERSION, logprefix, "featureProduct:"+featureProduct+" famousProduct:"+famousProductList.get(z));
                            if (featureProduct.getId().equals(famousProductList.get(z).getId())) {
                                //already exist
                                productExist=true;
                                break;
                            }
                        }
                        if (!productExist) {
                            famousProductList.add(featureProduct);
                        }
                        if (famousProductList.size()>=productLimit) {
                            break;
                        }
                    }
                }
            }
        }
        
        //set product discount
        for (int z=0;z<famousProductList.size();z++) {
            ProductMain famousProduct = famousProductList.get(z);
            Optional<ProductMain> productInfoOpt = productRepository.findById(famousProduct.getId());
            if (productInfoOpt.isPresent()) {
                ProductMain productInfo = productInfoOpt.get();
                Optional<StoreWithDetails> store = storeRepository.findById(productInfo.getStoreDetails().getId());                
                //get reqion country for store
                RegionCountry regionCountry = null;
                Optional<RegionCountry> optRegion = regionCountriesRepository.findById(store.get().getRegionCountryId());
                if (optRegion.isPresent()) {
                    regionCountry = optRegion.get();
                }
                if (productInfo.getProductInventories()!=null) {
                    for (int i=0;i<productInfo.getProductInventories().size();i++) {
                        ProductInventoryWithDetails inventoryDetails = productInfo.getProductInventories().get(i);
                        setProductDiscount(storeDiscountRepository, inventoryDetails, store.get().getId(), regionCountry );                              
                    }
                }                 
            }
        }
        
        HttpResponse response = new HttpResponse(request.getRequestURI());
        response.setData(famousProductList);
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
    private void  setProductDiscount(StoreDiscountRepository storeDiscountRepository, ProductInventoryWithDetails inventoryDetails, String storeId, RegionCountry regionCountry) {
        ItemDiscount discountDetails = ProductDiscount.getItemDiscount(storeDiscountRepository, storeId, inventoryDetails.getItemCode(), regionCountry);
        if (discountDetails != null) {                    
            double discountedPrice = inventoryDetails.getPrice();
            double dineInDiscountedPrice = inventoryDetails.getDineInPrice();

            if (discountDetails.calculationType.equals(DiscountCalculationType.FIX)) {
                discountedPrice = inventoryDetails.getPrice() - discountDetails.discountAmount;
            } else if (discountDetails.calculationType.equals(DiscountCalculationType.PERCENT)) {
                discountedPrice = inventoryDetails.getPrice() - (discountDetails.discountAmount / 100 * inventoryDetails.getPrice());
            }

            if(discountDetails.dineInCalculationType!=null && discountDetails.dineInCalculationType.equals(DiscountCalculationType.FIX)){
                dineInDiscountedPrice = inventoryDetails.getDineInPrice() - discountDetails.dineInDiscountAmount;

            }
            else if (discountDetails.dineInCalculationType!=null && discountDetails.dineInCalculationType.equals(DiscountCalculationType.PERCENT)) {
                dineInDiscountedPrice = inventoryDetails.getDineInPrice() - (discountDetails.dineInDiscountAmount / 100 * inventoryDetails.getDineInPrice());
            }

            discountDetails.discountedPrice = discountedPrice;
            discountDetails.normalPrice = inventoryDetails.getPrice();  

            discountDetails.dineInDiscountedPrice= dineInDiscountedPrice;
            discountDetails.dineInNormalPrice = inventoryDetails.getDineInPrice();

            inventoryDetails.setItemDiscount(discountDetails); 
            
            Logger.application.info(Logger.pattern, LocationServiceApplication.VERSION, "setProductDiscount", "Set item dicount for item:"+inventoryDetails.getItemCode()+" -> "+discountDetails.toString());
        }         
    }

}
