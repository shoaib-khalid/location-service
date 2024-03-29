package com.kalsym.locationservice.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kalsym.locationservice.model.TagKeyword;
import com.kalsym.locationservice.model.TagKeywordDetails;
import com.kalsym.locationservice.service.TagKeywordService;
import com.kalsym.locationservice.utility.HttpResponse;

@RestController
@RequestMapping("")
public class TagController {

    @Autowired
    TagKeywordService tagKeywordService;

    @Value("${product.search.radius}")
    private Double searchRadius;

    @GetMapping(path = {"/tags"}, name = "store-customers-get")
    @PreAuthorize("hasAnyAuthority('store-customers-get', 'all')")
    public ResponseEntity<HttpResponse> getTags(
        HttpServletRequest request,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(required = false) String latitude,
        @RequestParam(required = false) String longitude,
        @RequestParam(required = false) String tagKeyword,
        @RequestParam(required = false, defaultValue = "keyword") String sortByCol,
        @RequestParam(required = false, defaultValue = "ASC") Sort.Direction sortingOrder
    ) {

        // List<TagKeyword> body = tagKeywordService.getTagList();
        Page<TagKeywordDetails> body = tagKeywordService.getTagListWithPageable(page,pageSize,latitude,longitude,tagKeyword,searchRadius,sortByCol,sortingOrder);
        
        HttpResponse response = new HttpResponse(request.getRequestURI());
        response.setData(body);
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);

    }
    
}
