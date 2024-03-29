package com.kalsym.locationservice.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import javax.persistence.OneToOne;
import javax.persistence.JoinColumn;
import com.fasterxml.jackson.annotation.JsonInclude;

@Getter
@Setter
@Entity
@Table(name = "region_city")
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)

public class RegionCity {
    
    @Id
    private String id;
    private String name;

    @OneToOne()
    @JoinColumn(name = "regionStateId",referencedColumnName="id")
    private RegionCountryState regionCountryState; 

}

