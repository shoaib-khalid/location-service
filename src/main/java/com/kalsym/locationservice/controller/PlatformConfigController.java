package com.kalsym.locationservice.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.kalsym.locationservice.LocationServiceApplication;
import com.kalsym.locationservice.model.PlatformConfig;
import com.kalsym.locationservice.repository.PlatformConfigRepository;
import com.kalsym.locationservice.service.PlatformConfigService;
import com.kalsym.locationservice.utility.HttpResponse;
import com.kalsym.locationservice.utility.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/platform")
public class PlatformConfigController {
    
    @Autowired
    PlatformConfigService platformConfigService; 

    @Autowired
    PlatformConfigRepository platformConfigRepository; 

    //Get All
    // @GetMapping(value={""})
    // public ResponseEntity<List<PlatformConfig>> getListPlatformConfig(
    //     HttpServletRequest request
    // ) {

    //     String logprefix = request.getRequestURI();
    //     Logger.application.info(Logger.pattern, TemplateServiceApplication.VERSION, logprefix, "", "");


    //     List<PlatformConfig> body = platformConfigService.getPlatformConfigs();

    //     return new ResponseEntity<>(body,HttpStatus.OK);

    // }

    /// Get By Id
    @GetMapping(value="/{platformId}")
    public ResponseEntity<Optional<PlatformConfig>> readPlatformConfigId(@PathVariable(value = "platformId") String platformId) {

        HttpStatus httpStatus;
        Optional<PlatformConfig> body = platformConfigService.getPlatformConfigId(platformId);

        if(body.isPresent()){
            httpStatus = HttpStatus.OK;
        } else{
            httpStatus = HttpStatus.NOT_FOUND;

        }
        return new ResponseEntity<>(body, httpStatus);
    }

    @GetMapping(path = {""}, name = "region-countries-get", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('region-countries-get', 'all')")
    public ResponseEntity<HttpResponse> getConfigbyDomain(HttpServletRequest request,
            @RequestParam(required = false) String domain) {
        String logprefix = request.getRequestURI();
        HttpResponse response = new HttpResponse(request.getRequestURI());
        Logger.application.info(Logger.pattern, LocationServiceApplication.VERSION, logprefix, "", "");

        // List<PlatformConfig> configList = platformConfigRepository.findByDomain(domain);
        List<PlatformConfig> configList;

        System.out.println("CHECKING getConfigbyDomain :::::::"+domain);

        configList = domain == null ? platformConfigRepository.findAll() : platformConfigRepository.findByDomain(domain);

        if (configList.isEmpty()) {
            response.setStatus(HttpStatus.NOT_FOUND);
        } else {
            response.setData(configList);
            response.setStatus(HttpStatus.OK);
        }
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
