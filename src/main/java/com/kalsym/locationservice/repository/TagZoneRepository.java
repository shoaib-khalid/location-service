package com.kalsym.locationservice.repository;

import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kalsym.locationservice.model.TagZone;

@Repository
public interface TagZoneRepository extends JpaRepository<TagZone,Integer> {    
    List<TagZone> findByTagId(Integer tagId);
}
