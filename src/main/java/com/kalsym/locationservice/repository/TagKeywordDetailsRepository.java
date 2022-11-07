package com.kalsym.locationservice.repository;

import com.kalsym.locationservice.model.TagKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.kalsym.locationservice.model.TagKeywordDetails;
import org.springframework.data.repository.query.Param;

@Repository
public interface TagKeywordDetailsRepository extends JpaRepository<TagKeywordDetails,Integer>, JpaSpecificationExecutor<TagKeywordDetails> {
    TagKeywordDetails findByKeyword(@Param("keyword") String keyword);
}
