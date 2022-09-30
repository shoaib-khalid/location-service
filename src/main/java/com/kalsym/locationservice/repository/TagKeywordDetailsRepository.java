package com.kalsym.locationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.kalsym.locationservice.model.TagKeywordDetails;

@Repository
public interface TagKeywordDetailsRepository extends JpaRepository<TagKeywordDetails,Integer>, JpaSpecificationExecutor<TagKeywordDetails> {
    
}
