package com.kalsym.locationservice.model.Discount;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.kalsym.locationservice.enums.DiscountCalculationType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 *
 * @author 7cu
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "store_discount_tier")
@NoArgsConstructor
public class StoreDiscountTier implements Serializable, Comparable< StoreDiscountTier > {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;
    
    private String storeDiscountId;
    private Double startTotalSalesAmount;
    private Double endTotalSalesAmount;
    private Double discountAmount;
    
    @Enumerated(EnumType.STRING)
    private DiscountCalculationType calculationType;
    
    @Override
    public int compareTo(StoreDiscountTier o) {
        return this.getStartTotalSalesAmount().compareTo(o.getStartTotalSalesAmount());
    }

}
