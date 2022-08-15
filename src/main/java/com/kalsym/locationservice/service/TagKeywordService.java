package com.kalsym.locationservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.kalsym.locationservice.model.TagKeyword;
import com.kalsym.locationservice.repository.TagKeywordRepository;

@Service

public class TagKeywordService {

    @Autowired
    TagKeywordRepository tagKeywordRepository;

    public List<TagKeyword> getTagList(){

        List<TagKeyword> result = tagKeywordRepository.findAll();
        
        return result;
        
    }

    public Page<TagKeyword> getTagListWithPageable(
        int page, int pageSize, String sortByCol, Sort.Direction sortingOrder
    ){
        Pageable pageable;

        if (sortingOrder==Sort.Direction.ASC){
            pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).ascending());
        } 
        else{
            pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).descending());
        } 

        Page<TagKeyword> result = tagKeywordRepository.findAll(pageable);       

        return result;
          
    }
    
}
