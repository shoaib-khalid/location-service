package com.kalsym.locationservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
    
}
