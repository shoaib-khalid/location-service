package com.kalsym.locationservice.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name = "store")
@Getter
@Setter
@JsonInclude(JsonInclude.Include.ALWAYS)
@ToString
@NoArgsConstructor
public class Store implements Serializable {

    @Id
    private String id;

    private String name;

    private String city;

    // private String state;
    
    @Column(name="regionCountryStateId")
    private String state;

    private String postcode;

    private String regionCountryId; 

}
