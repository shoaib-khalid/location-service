package com.kalsym.locationservice.utility;

/*
 * Copyright (C) 2021 taufik
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import com.kalsym.locationservice.LocationServiceApplication;
import com.kalsym.locationservice.enums.DiscountCalculationType;
import com.kalsym.locationservice.model.RegionCountry;
import com.kalsym.locationservice.model.Product.ItemDiscount;
import com.kalsym.locationservice.repository.StoreDiscountRepository;

/**
 *
 * @author taufik
 */
public class ProductDiscount {
    
    public static ItemDiscount getItemDiscount(StoreDiscountRepository storeDiscountRepository,
            String storeId, String itemCode, RegionCountry regionCountry) {
        
        ItemDiscount discountDetails = null;
        List<Object[]> t = storeDiscountRepository.getItemDiscount(itemCode, storeId);
        if (t!=null) {
            Object[] itemDiscount = t.get(0);
            String discountName = String.valueOf(itemDiscount[0]);
            if (!discountName.equalsIgnoreCase("NOTFOUND")) {
                discountDetails = new ItemDiscount();
                try {
                    //discountName, startDate, endDate, normalPriceItemOnly, discountAmount, calculationType, discountId
                    Logger.application.info(Logger.pattern, LocationServiceApplication.VERSION, "discountDetails:"+itemDiscount.toString());
                    discountDetails.discountLabel = String.valueOf(itemDiscount[0]);
                    Date date1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(String.valueOf(itemDiscount[1]));  
                    LocalDateTime startLocalTime = DateTimeUtil.convertToLocalDateTimeViaInstant(date1, ZoneId.of(regionCountry.getTimezone()) );
                    discountDetails.discountStartTime = startLocalTime;
                    Date date2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(String.valueOf(itemDiscount[2]));  
                    LocalDateTime endLocalTime = DateTimeUtil.convertToLocalDateTimeViaInstant(date2, ZoneId.of(regionCountry.getTimezone()) );
                    discountDetails.discountEndTime = endLocalTime;
                    if (String.valueOf(itemDiscount[3]).equals("true"))
                        discountDetails.normalItemOnly = true;
                    else
                        discountDetails.normalItemOnly = false;
                    discountDetails.discountAmount = Double.parseDouble(String.valueOf(itemDiscount[4]));
                    discountDetails.calculationType = DiscountCalculationType.valueOf(String.valueOf(itemDiscount[5]));
                    discountDetails.discountId =  String.valueOf(itemDiscount[6]);
                    discountDetails.dineInDiscountAmount = Double.parseDouble(String.valueOf(itemDiscount[7]));
                    discountDetails.dineInCalculationType = DiscountCalculationType.valueOf(String.valueOf(itemDiscount[8]));
                } catch (Exception ex){
                    Logger.application.error(Logger.pattern, LocationServiceApplication.VERSION, "getItemDiscount", "Error extracting discount details : ", ex);
                }
            }
        }
        
        return discountDetails;
    }
                    
}

