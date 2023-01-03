package com.kalsym.locationservice.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kalsym.locationservice.model.TagTable;
@Repository

public interface TagTableRepository extends JpaRepository<TagTable,Integer> {
    
}
