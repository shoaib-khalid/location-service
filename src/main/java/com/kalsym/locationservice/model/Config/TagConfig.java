package com.kalsym.locationservice.model.Config;

import javax.persistence.Entity;
import javax.persistence.Id;

import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "tag_config")
@Getter
@Setter
@JsonInclude(JsonInclude.Include.ALWAYS)
@ToString
@NoArgsConstructor
public class TagConfig {

    @Id
    private Integer id;

    private String property;

    private String content;

    private Integer tagId;

}
