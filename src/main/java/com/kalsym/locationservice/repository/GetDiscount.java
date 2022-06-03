package com.kalsym.locationservice.repository;

import java.util.List;

import com.kalsym.locationservice.enums.DiscountCalculationType;
import com.kalsym.locationservice.model.RegionCountry;
import com.kalsym.locationservice.model.Discount.StoreDiscountProduct;
import com.kalsym.locationservice.model.Product.ItemDiscount;
import com.kalsym.locationservice.model.Product.ProductInventoryWithDetails;
import com.kalsym.locationservice.model.Product.ProductMain;
import com.kalsym.locationservice.utility.ProductDiscount;

//non generic 
public class GetDiscount {

    public static ProductMain[] getProductDiscountList (
        List<ProductMain> productList,
        RegionCountry regionCountry,
        StoreDiscountRepository storeDiscountRepository,
        StoreDiscountProductRepository storeDiscountProductRepository
    ){
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
        return productWithDetailsList;
    }
    
}
