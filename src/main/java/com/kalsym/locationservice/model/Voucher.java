package com.kalsym.locationservice.model;
import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

import com.kalsym.locationservice.enums.DiscountCalculationType;
import com.kalsym.locationservice.enums.VoucherType;
import com.kalsym.locationservice.enums.VoucherStatus;
import com.kalsym.locationservice.enums.VoucherGroupType;
import com.kalsym.locationservice.enums.VoucherDiscountType;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

/**
 *
 * @author ayaan
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "voucher")
@NoArgsConstructor
public class Voucher implements Serializable{

    @Id
    private String id;
    private String storeId;
    private String name;

    private Double discountValue;
    private Double maxDiscountAmount;
    private String voucherCode;
    private Integer totalQuantity;
    private Integer totalRedeem;
    private String currencyLabel;
    private Boolean isNewUserVoucher;
    private Boolean checkTotalRedeem;
    private Double minimumSpend;
    private Boolean allowDoubleDiscount;
    private Boolean requireToClaim;

    @Enumerated(EnumType.STRING)
    private VoucherStatus status;

    @Enumerated(EnumType.STRING)
    private VoucherType voucherType;

    @Enumerated(EnumType.STRING)
    private VoucherDiscountType discountType;

    @Enumerated(EnumType.STRING)
    private VoucherGroupType groupType;

    @Enumerated(EnumType.STRING)
    private DiscountCalculationType calculationType;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startDate;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endDate;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucherId", insertable = false, updatable = false, nullable = true)
    private List<VoucherTerms> voucherTerms;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucherId", insertable = false, updatable = false, nullable = true)
    private List<VoucherVertical> voucherVerticalList;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucherId", insertable = false, updatable = false, nullable = true)
    private List<VoucherStore> voucherStoreList;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucherId", insertable = false, updatable = false, nullable = true)
    private List<VoucherServiceType> voucherServiceTypeList;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucherId", insertable = false, updatable = false, nullable = true)
    private List<VoucherSerialNumber> voucherSerialNumber;


    public void update(Voucher bodyVoucher) {
        if (bodyVoucher == null) {
            return;
        }

        // Update fields only if they are not null in the bodyVoucher
        if (bodyVoucher.getStoreId() != null) {
            this.setStoreId(bodyVoucher.getStoreId());
        }
        if (bodyVoucher.getName() != null) {
            this.setName(bodyVoucher.getName());
        }
        if (bodyVoucher.getDiscountValue() != null) {
            this.setDiscountValue(bodyVoucher.getDiscountValue());
        }
        if (bodyVoucher.getMaxDiscountAmount() != null) {
            this.setMaxDiscountAmount(bodyVoucher.getMaxDiscountAmount());
        }
        if (bodyVoucher.getVoucherCode() != null) {
            this.setVoucherCode(bodyVoucher.getVoucherCode());
        }
        if (bodyVoucher.getTotalQuantity() != null) {
            this.setTotalQuantity(bodyVoucher.getTotalQuantity());
        }
        if (bodyVoucher.getTotalRedeem() != null) {
            this.setTotalRedeem(bodyVoucher.getTotalRedeem());
        }
        if (bodyVoucher.getCurrencyLabel() != null) {
            this.setCurrencyLabel(bodyVoucher.getCurrencyLabel());
        }
        if (bodyVoucher.getIsNewUserVoucher() != null) {
            this.setIsNewUserVoucher(bodyVoucher.getIsNewUserVoucher());
        }
        if (bodyVoucher.getCheckTotalRedeem() != null) {
            this.setCheckTotalRedeem(bodyVoucher.getCheckTotalRedeem());
        }
        if (bodyVoucher.getMinimumSpend() != null) {
            this.setMinimumSpend(bodyVoucher.getMinimumSpend());
        }
        if (bodyVoucher.getAllowDoubleDiscount() != null) {
            this.setAllowDoubleDiscount(bodyVoucher.getAllowDoubleDiscount());
        }
        if (bodyVoucher.getRequireToClaim() != null) {
            this.setRequireToClaim(bodyVoucher.getRequireToClaim());
        }
        if (bodyVoucher.getStatus() != null) {
            this.setStatus(bodyVoucher.getStatus());
        }
        if (bodyVoucher.getVoucherType() != null) {
            this.setVoucherType(bodyVoucher.getVoucherType());
        }
        if (bodyVoucher.getDiscountType() != null) {
            this.setDiscountType(bodyVoucher.getDiscountType());
        }
        if (bodyVoucher.getCalculationType() != null) {
            this.setCalculationType(bodyVoucher.getCalculationType());
        }
        if (bodyVoucher.getStartDate() != null) {
            this.setStartDate(bodyVoucher.getStartDate());
        }
        if (bodyVoucher.getEndDate() != null) {
            this.setEndDate(bodyVoucher.getEndDate());
        }
    }
}
