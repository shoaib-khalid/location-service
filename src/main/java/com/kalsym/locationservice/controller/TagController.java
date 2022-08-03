package com.kalsym.locationservice.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kalsym.locationservice.model.TagKeyword;
import com.kalsym.locationservice.service.TagKeywordService;
import com.kalsym.locationservice.utility.HttpResponse;

@RestController
@RequestMapping("")
public class TagController {

    @Autowired
    TagKeywordService tagKeywordService;

    @GetMapping(path = {"/tags"}, name = "store-customers-get")
    @PreAuthorize("hasAnyAuthority('store-customers-get', 'all')")
    public ResponseEntity<HttpResponse> getTags(
        HttpServletRequest request
    ) {

        List<TagKeyword> body = tagKeywordService.getTagList();
        
        HttpResponse response = new HttpResponse(request.getRequestURI());
        response.setData(body);
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);

    }
    
}
