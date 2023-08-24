package com.kalsym.locationservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 *
 * @author ayaan
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "voucher_vertical")
@NoArgsConstructor
public class VoucherVertical implements Serializable {

    @Id
    private String id;

    private String voucherId;
    private String verticalCode;

}