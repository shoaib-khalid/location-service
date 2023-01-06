package com.kalsym.locationservice.controller;

import com.kalsym.locationservice.LocationServiceApplication;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Comparator;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

import io.swagger.annotations.ApiOperation;

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

            //nested sort , sort the zone first then sort table 
            List<TagZone> sortedDataTableZoneList = tableZoneList.stream() 
            .sorted(Comparator.comparing(TagZone::getZoneName))
            .map((TagZone mapper)->{
            
                //nullsLast,nullsFirst to handling null error
                List<TagTable> tagTableDetails = mapper.getTagTables().stream()
                .sorted(Comparator.comparing(TagTable::getTablePrefix, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());

                mapper.setTagTables(tagTableDetails);
                return mapper;

            })
            .collect(Collectors.toList());

            response.setData(sortedDataTableZoneList);
            response.setStatus(HttpStatus.OK);
        } else {
            response.setData(tagStoreDetails);

            response.setStatus(HttpStatus.OK);
        }        
                
        return ResponseEntity.status(response.getStatus()).body(response);

    }

    // @PostMapping(path = {"/tags/tables/postBulk"}, name = "store-customers-get")
    // @PreAuthorize("hasAnyAuthority('stores-post', 'all')")
    // public ResponseEntity<HttpResponse> postTagZoneTable(
    //     HttpServletRequest request,
    //     @RequestBody TagZoneTableRequest tagZoneTableRequest

    // ) {
        
    //     TagZone dataTagZone;

    //     TagZone body = TagZone.castReference(tagZoneTableRequest);
    //     if(tagZoneTableRequest.getId()!= null){
    //         dataTagZone = tagKeywordService.updateTagZone(tagZoneTableRequest.getId(),body);
 
    //     } else{
    //          dataTagZone = tagKeywordService.createTagZone(body);

    //     }

    //     List<TagTableRequest> tagTableReq = tagZoneTableRequest.getTagTable();
        
    //     List<TagTable> tagTable  = tagTableReq.stream()
    //     .map((TagTableRequest x)->{

    //         TagTable tagTableCastRef = TagTable.castReference(x);
    //         tagTableCastRef.setZoneId(dataTagZone.getId());

    //         TagTable dataTagTable = tagKeywordService.createTagTable(tagTableCastRef);


    //         return dataTagTable;
    //     })
    //     .collect(Collectors.toList());
        
    //     dataTagZone.setTagTables(tagTable);

    //     HttpResponse response = new HttpResponse(request.getRequestURI());
    //     response.setData(body);
    //     response.setStatus(HttpStatus.OK);
    //     return ResponseEntity.status(response.getStatus()).body(response);

    // }

    @ApiOperation(value = "Create table zone", notes = "The request body need to exclude List<TagTableRequest> tagTable.")
    @PostMapping(path = {"/tag/zone"}, name = "store-customers-get")
    @PreAuthorize("hasAnyAuthority('stores-post', 'all')")
    public ResponseEntity<HttpResponse> postTagZone(
        HttpServletRequest request,
        @RequestBody TagZoneTableRequest tagZoneTableRequest
    ) {
        HttpResponse response = new HttpResponse(request.getRequestURI());

        try {
            TagZone body = TagZone.castReference(tagZoneTableRequest);

            TagZone dataTagZone = tagKeywordService.createTagZone(body);
    
            response.setData(dataTagZone);
            response.setStatus(HttpStatus.OK);
            
        } catch (Exception e) {
            // TODO: handle exception
            response.setStatus(HttpStatus.EXPECTATION_FAILED);//error code 417

        }
        return ResponseEntity.status(response.getStatus()).body(response);


    }

    @ApiOperation(value = "Edit table zone", notes = "The request body need to exclude List<TagTableRequest> tagTable.")
    @PutMapping(path = {"/tag/zone/{id}"}, name = "store-customers-get")
    @PreAuthorize("hasAnyAuthority('stores-post', 'all')")
    public ResponseEntity<HttpResponse> putTagZone(
        HttpServletRequest request,
        @PathVariable(required = true) Integer id,
        @RequestBody TagZoneTableRequest tagZoneTableRequest
    ) {
        HttpResponse response = new HttpResponse(request.getRequestURI());

        try {
            TagZone body = TagZone.castReference(tagZoneTableRequest);

            TagZone dataTagZone = tagKeywordService.updateTagZone(id,body);
    
            response.setData(dataTagZone);
            response.setStatus(HttpStatus.OK);
        } catch (Exception e) {
            // TODO: handle exception
            response.setStatus(HttpStatus.EXPECTATION_FAILED);//error code 417

        }
        return ResponseEntity.status(response.getStatus()).body(response);

    }

    @DeleteMapping(path = {"tag/zone/{id}"})
    public ResponseEntity<HttpResponse> deleteTagZone(
        HttpServletRequest request, 
        @PathVariable Integer id
    ){

        HttpStatus httpStatus;
        HttpResponse response = new HttpResponse(request.getRequestURI());

        try {
        
    
            Boolean isDeleted = tagKeywordService.deleteTagZone(id);
    
            httpStatus = isDeleted ? HttpStatus.OK :HttpStatus.NOT_FOUND;
    
            response.setStatus(httpStatus);
            
        } catch (Exception e) {
            // TODO: handle exception
            response.setStatus(HttpStatus.EXPECTATION_FAILED);//error code 417

        }
        return ResponseEntity.status(response.getStatus()).body(response);

    }

    @ApiOperation(value = "Create tag table", notes = "Refer the request body exactly")
    @PostMapping(path = {"/tag/table"}, name = "store-customers-get")
    @PreAuthorize("hasAnyAuthority('stores-post', 'all')")
    public ResponseEntity<HttpResponse> postTagTable(
        HttpServletRequest request,
        @RequestBody TagTableRequest tagTableRequest
    ) {
        HttpResponse response = new HttpResponse(request.getRequestURI());

        try {
            TagTable body = TagTable.castReference(tagTableRequest);

            TagTable dataTagTable = tagKeywordService.createTagTable(body);
    
            response.setData(dataTagTable);
            response.setStatus(HttpStatus.OK);
            
        } catch (Exception e) {
            // TODO: handle exception
            response.setStatus(HttpStatus.EXPECTATION_FAILED);//error code 417

        }
        return ResponseEntity.status(response.getStatus()).body(response);

    }

    @ApiOperation(value = "Create tag table", notes = "Refer the request body exactly")
    @PostMapping(path = {"/tag/table/bulk"}, name = "store-customers-get")
    @PreAuthorize("hasAnyAuthority('stores-post', 'all')")
    public ResponseEntity<HttpResponse> postTagTableBulk(
        HttpServletRequest request,
        @RequestBody List<TagTableRequest> tagTableRequest
    ) {
        HttpResponse response = new HttpResponse(request.getRequestURI());

        try {

                List<TagTable> tagTable  = tagTableRequest.stream()
                .map((TagTableRequest x)->{

                    TagTable tagTableCastRef = TagTable.castReference(x);

                    TagTable dataTagTable = tagKeywordService.createTagTable(tagTableCastRef);


                    return dataTagTable;
                })
                .collect(Collectors.toList());


    
            response.setData(tagTable);
            response.setStatus(HttpStatus.OK);
            
        } catch (Exception e) {
            // TODO: handle exception
            response.setStatus(HttpStatus.EXPECTATION_FAILED);//error code 417

        }
        return ResponseEntity.status(response.getStatus()).body(response);

    }

    @ApiOperation(value = "Edit table tag", notes = "Refer the request body exactly")
    @PutMapping(path = {"/tag/table/{id}"}, name = "store-customers-get")
    @PreAuthorize("hasAnyAuthority('stores-post', 'all')")
    public ResponseEntity<HttpResponse> putTagTable(
        HttpServletRequest request,
        @PathVariable(required = true) Integer id,
        @RequestBody TagTableRequest tagTableRequest
    ) {
        HttpResponse response = new HttpResponse(request.getRequestURI());

        try {
            TagTable body = TagTable.castReference(tagTableRequest);

            TagTable dataTagTable = tagKeywordService.updateTagTable(id,body);
    
            response.setData(dataTagTable);
            response.setStatus(HttpStatus.OK);
        } catch (Exception e) {
            // TODO: handle exception
            response.setStatus(HttpStatus.EXPECTATION_FAILED);//error code 417

        }
        return ResponseEntity.status(response.getStatus()).body(response);

    }

    @DeleteMapping(path = {"tag/table/{id}"})
    public ResponseEntity<HttpResponse> deleteTagTable(
        HttpServletRequest request, 
        @PathVariable Integer id
    ){

        HttpStatus httpStatus;
        HttpResponse response = new HttpResponse(request.getRequestURI());

        try {
        
    
            Boolean isDeleted = tagKeywordService.deleteTagTable(id);
    
            httpStatus = isDeleted ? HttpStatus.OK :HttpStatus.NOT_FOUND;
    
            response.setStatus(httpStatus);
            
        } catch (Exception e) {
            // TODO: handle exception
            response.setStatus(HttpStatus.EXPECTATION_FAILED);//error code 417

        }
        return ResponseEntity.status(response.getStatus()).body(response);

    }

    @GetMapping(path = {"/tags/details"}, name = "store-customers-get")
    @PreAuthorize("hasAnyAuthority('store-customers-get', 'all')")
    public ResponseEntity<HttpResponse> toGetDetailsByStoreId(
        HttpServletRequest request,
        @RequestParam(required = true) String storeId        
    ) {
        String logprefix = request.getRequestURI() + " ";
        HttpResponse response = new HttpResponse(request.getRequestURI());
       
        //get tag for this store
        List<TagStoreDetails> tagStoreDetails = tagStoreDetailsRepository.findByStoreId(storeId);
        response.setData(tagStoreDetails);
        response.setStatus(HttpStatus.OK);
   
                
        return ResponseEntity.status(response.getStatus()).body(response);

    }

    @GetMapping(path = {"/tags/generateTagTable"}, name = "store-customers-get")
    @PreAuthorize("hasAnyAuthority('store-customers-get', 'all')")
    public ResponseEntity<HttpResponse> generateTagTable(
        HttpServletRequest request,
        @RequestParam(required = true) String tablePrefix, 
        @RequestParam(required = true) Integer tableStart,
        @RequestParam(required = true) Integer tableEnd
    ) {
        String logprefix = request.getRequestURI() + " ";
        HttpResponse response = new HttpResponse(request.getRequestURI());

        try {

            if(tableStart>tableEnd){
                response.setStatus(HttpStatus.EXPECTATION_FAILED);
                response.setMessage("Table Start must be less than Table End");
                return ResponseEntity.status(response.getStatus()).body(response);

            }
            List<TagTableRequest> tagTableReq = new ArrayList<>();
            for (int i=tableStart;i<=tableEnd;i++) {
    
                TagTableRequest tagTableRequest= new TagTableRequest();
                tagTableRequest.setTablePrefix(tablePrefix);
                tagTableRequest.setTableNumber(String.valueOf(i));
                tagTableRequest.setCombinationTableNumber(tablePrefix== null?""+String.valueOf(i):tablePrefix+String.valueOf(i));
    
                tagTableReq.add(tagTableRequest);
                
            }  
    
            response.setData(tagTableReq);
            response.setStatus(HttpStatus.OK);
            
        } catch (Exception e) {
            // TODO: handle exception
            response.setStatus(HttpStatus.EXPECTATION_FAILED);

        }
        return ResponseEntity.status(response.getStatus()).body(response);


    }
    
}
