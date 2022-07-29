package com.kalsym.locationservice.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatcher;
import org.springframework.stereotype.Service;

import com.kalsym.locationservice.model.RegionCountry;
import com.kalsym.locationservice.model.StoreSnooze;
import com.kalsym.locationservice.model.TagKeyword;
import com.kalsym.locationservice.model.TagStoreDetails;
import com.kalsym.locationservice.repository.RegionCountriesRepository;
import com.kalsym.locationservice.repository.TagStoreDetailsRepository;
import com.kalsym.locationservice.utility.DateTimeUtil;

@Service
public class StoreService {

    @Autowired
    TagStoreDetailsRepository tagStoreDetailsRepository;
    
    @Autowired
    RegionCountriesRepository regionCountriesRepository;

    // public Page<TagStoreDetails> getQueryStoreTag(String keyword,int page, int pageSize){

    //     TagKeyword tagKeyword = new TagKeyword();
    //     tagKeyword.setKeyword(keyword);

    //     TagStoreDetails tagStoreDetailsMatch = new TagStoreDetails();
    //     tagStoreDetailsMatch.setTagKeyword(tagKeyword);
      
    //     ExampleMatcher matcher = ExampleMatcher
    //             .matchingAll()
    //             .withIgnoreCase()
    //             .withMatcher("keyword", new GenericPropertyMatcher().exact())
    //             .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
    //     Example<TagStoreDetails> example = Example.of(tagStoreDetailsMatch, matcher);

    //     Pageable pageable= PageRequest.of(page, pageSize);
   

    //     Page<TagStoreDetails> result = tagStoreDetailsRepository.findAll(example,pageable);

    //     //to set snooze
    //     for(TagStoreDetails tagStore : result){

    //         StoreSnooze st = new StoreSnooze();

    //         if (tagStore.getStoreDetails().getSnoozeStartTime()!=null && tagStore.getStoreDetails().getSnoozeEndTime()!=null) {
    //             int resultSnooze = tagStore.getStoreDetails().getSnoozeEndTime().compareTo(Calendar.getInstance().getTime());
    //             if (resultSnooze < 0) {
    //                 tagStore.getStoreDetails().setIsSnooze(false);

    //                 st.snoozeStartTime = null;
    //                 st.snoozeEndTime = null;
    //                 st.isSnooze = false;
    //                 st.snoozeReason = null;
    //                 tagStore.getStoreDetails().setStoreSnooze(st);

    //             } else {
            
    //                 tagStore.getStoreDetails().setIsSnooze(true);

    //                 Optional<RegionCountry> t = regionCountriesRepository.findById(tagStore.getStoreDetails().getRegionCountryId());

    //                 if(t.isPresent()){
    //                     LocalDateTime startTime = DateTimeUtil.convertToLocalDateTimeViaInstant(tagStore.getStoreDetails().getSnoozeStartTime(), ZoneId.of(t.get().getTimezone()));
    //                     LocalDateTime endTime = DateTimeUtil.convertToLocalDateTimeViaInstant(tagStore.getStoreDetails().getSnoozeEndTime(), ZoneId.of(t.get().getTimezone()));
                        
    //                     st.snoozeStartTime = startTime;
    //                     st.snoozeEndTime = endTime;
    //                     st.isSnooze = true;
    //                     st.snoozeReason = tagStore.getStoreDetails().getSnoozeReason();

    //                     tagStore.getStoreDetails().setStoreSnooze(st);
    //                 }
         
    //             }
    //         } else {
    //             tagStore.getStoreDetails().setIsSnooze(false);

                
    //             st.snoozeStartTime = null;
    //             st.snoozeEndTime = null;
    //             st.isSnooze = false;
    //             st.snoozeReason = null;
    //             tagStore.getStoreDetails().setStoreSnooze(st);

    //         }
            
    //     }

    //     return result;

    // }
    
}
