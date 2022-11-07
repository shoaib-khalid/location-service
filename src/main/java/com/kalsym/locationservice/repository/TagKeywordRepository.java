package com.kalsym.locationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.kalsym.locationservice.model.TagKeyword;
import com.kalsym.locationservice.model.TagStoreDetails;
import org.springframework.data.repository.query.Param;

@Repository
public interface TagKeywordRepository extends JpaRepository<TagKeyword,Integer>, JpaSpecificationExecutor<TagKeyword> {
    
    
}
