package com.kalsym.locationservice.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kalsym.locationservice.model.Config.TagConfig;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "tag_keyword")
@Getter
@Setter
@JsonInclude(JsonInclude.Include.ALWAYS)
@ToString
@NoArgsConstructor
public class TagKeywordDetails {

    @Id
    private Integer id;

    private String keyword;

    private String longitude;

    private String latitude;

    @OneToMany(cascade = CascadeType.ALL,
    fetch = FetchType.LAZY)
    @JoinColumn(name = "tagId", insertable = false, updatable = false, nullable = true)    
    private List<TagConfig> tagConfig; 
}
