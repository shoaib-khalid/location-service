package com.kalsym.locationservice.controller;

import com.kalsym.locationservice.LocationServiceApplication;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kalsym.locationservice.model.TagKeyword;
import com.kalsym.locationservice.model.TagTable;
import com.kalsym.locationservice.model.TagTableRequest;
import com.kalsym.locationservice.model.TagZone;
import com.kalsym.locationservice.model.TagZoneTableRequest;
import com.kalsym.locationservice.model.TagKeywordDetails;
import com.kalsym.locationservice.model.TagStoreDetails;
import com.kalsym.locationservice.service.TagKeywordService;
import com.kalsym.locationservice.utility.HttpResponse;

import com.kalsym.locationservice.repository.TagStoreDetailsRepository;
import com.kalsym.locationservice.repository.TagZoneRepository;
import com.kalsym.locationservice.utility.Logger;

@RestController
@RequestMapping("")
public class TagController {

    @Autowired
    TagKeywordService tagKeywordService;
    
    @Autowired
    TagStoreDetailsRepository tagStoreDetailsRepository;
    
    @Autowired
    TagZoneRepository tagZoneRepository;
    
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
    
    
    @GetMapping(path = {"/tags/tables"}, name = "store-customers-get")
    @PreAuthorize("hasAnyAuthority('store-customers-get', 'all')")
    public ResponseEntity<HttpResponse> getTables(
        HttpServletRequest request,
        @RequestParam(required = true) String storeId        
    ) {
        String logprefix = request.getRequestURI() + " ";
        HttpResponse response = new HttpResponse(request.getRequestURI());
       
        //get tag for this store
        List<TagStoreDetails> tagStoreDetails = tagStoreDetailsRepository.findByStoreId(storeId);
        if (tagStoreDetails!=null && tagStoreDetails.size()>0) {
            TagStoreDetails tagStoreDetail = tagStoreDetails.get(0);
            //get table list for this tag
            Logger.application.info(Logger.pattern, LocationServiceApplication.VERSION, logprefix, "Tag found:"+tagStoreDetail.getTagId());
            List<TagZone> tableZoneList = tagZoneRepository.findByTagId(tagStoreDetail.getTagId());
            for (int i=0;i<tableZoneList.size();i++) {
                TagZone tagZone = tableZoneList.get(i);
                for (int x=0;x<tagZone.getTagTables().size();x++) {
                    TagTable tagTable = tagZone.getTagTables().get(x);
                    int tblNo = tagTable.getTableNoStart();
                    List<String> tableNoList = new ArrayList();
                    int count=0;
                    while (tblNo <= tagTable.getTableNoEnd()) {
                        String tblNoStr = String.valueOf(tblNo);
                        if (tagTable.getTablePrefix()!=null) {
                            tblNoStr = tagTable.getTablePrefix()+tblNoStr;
                        }                        
                        tableNoList.add(tblNoStr);
                        tblNo++;
                        count++;
                        if (count>1000) {break;}
                    }
                    tagTable.setTableNoList(tableNoList);
                }
            }
            response.setData(tableZoneList);
            response.setStatus(HttpStatus.OK);
        } else {
            response.setStatus(HttpStatus.NOT_FOUND);
        }        
                
        return ResponseEntity.status(response.getStatus()).body(response);

    }

    @PostMapping(path = {"/tags/createEdit"}, name = "store-customers-get")
    @PreAuthorize("hasAnyAuthority('stores-post', 'all')")
    public ResponseEntity<HttpResponse> postTagZoneTable(
        HttpServletRequest request,
        @RequestBody TagZoneTableRequest tagZoneTableRequest

    ) {
        
        TagZone dataTagZone;

        TagZone body = TagZone.castReference(tagZoneTableRequest);
        if(tagZoneTableRequest.getId()!= null){
            dataTagZone = tagKeywordService.updateTagZone(tagZoneTableRequest.getId(),body);
 
        } else{
             dataTagZone = tagKeywordService.createTagZone(body);

        }

        List<TagTableRequest> tagTableReq = tagZoneTableRequest.getTagTable();
        
        List<TagTable> tagTable  = tagTableReq.stream()
        .map((TagTableRequest x)->{

            TagTable tagTableCastRef = TagTable.castReference(x);
            tagTableCastRef.setZoneId(dataTagZone.getId());

            TagTable dataTagTable;
            if(tagZoneTableRequest.getId()!= null){
                dataTagTable = tagKeywordService.updateTagTable(tagZoneTableRequest.getId(),tagTableCastRef);
     
            } else{
                dataTagTable = tagKeywordService.createTagTable(tagTableCastRef);

            }

            return dataTagTable;
        })
        .collect(Collectors.toList());
        
        dataTagZone.setTagTables(tagTable);

        HttpResponse response = new HttpResponse(request.getRequestURI());
        response.setData(body);
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);

    }
    
}
