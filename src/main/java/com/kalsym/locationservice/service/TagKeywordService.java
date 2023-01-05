package com.kalsym.locationservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.kalsym.locationservice.model.TagKeyword;
import com.kalsym.locationservice.model.TagKeywordDetails;
import com.kalsym.locationservice.model.TagTable;
import com.kalsym.locationservice.model.TagZone;
import com.kalsym.locationservice.repository.TagKeywordDetailsRepository;
import com.kalsym.locationservice.repository.TagKeywordRepository;
import com.kalsym.locationservice.repository.TagTableRepository;
import com.kalsym.locationservice.repository.TagZoneRepository;

import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Coordinate;
import org.hibernate.spatial.predicate.SpatialPredicates;

@Service

public class TagKeywordService {

    @Autowired
    TagKeywordRepository tagKeywordRepository;

    @Autowired
    TagKeywordDetailsRepository tagKeywordDetailsRepository;

    @Autowired
    TagZoneRepository tagZoneRepository;

    @Autowired
    TagTableRepository tagTableRepository;

    public List<TagKeyword> getTagList(){

        List<TagKeyword> result = tagKeywordRepository.findAll();
        
        return result;
        
    }

    public Page<TagKeywordDetails> getTagListWithPageable(
        int page, int pageSize, String latitude,String longitude, String tagKeyword,double searchRadius,String sortByCol, Sort.Direction sortingOrder
    ){
        Pageable pageable;

        TagKeywordDetails tagKeywordMatch = new TagKeywordDetails();

        ExampleMatcher matcher = ExampleMatcher
        .matchingAll()
        .withIgnoreCase()
        .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
        Example<TagKeywordDetails> example = Example.of(tagKeywordMatch, matcher);

        if (sortingOrder==Sort.Direction.ASC){
            pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).ascending());
        } 
        else{
            pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).descending());
        } 
        Specification<TagKeywordDetails> tagKeywordSpecs = searchStoreCategorySpecs(latitude, longitude,tagKeyword,searchRadius,example);

        Page<TagKeywordDetails> result = tagKeywordDetailsRepository.findAll(tagKeywordSpecs,pageable);       

        return result;
          
    }
    

    public static Specification<TagKeywordDetails> searchStoreCategorySpecs(
            String latitude, 
            String longitude,
            String tagKeyword,
            double radius,        
            Example<TagKeywordDetails> example) {
    
        return (Specification<TagKeywordDetails>) (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();
       

            if (latitude!=null && longitude!=null) {
                Expression<Point> point1 = builder.function("point", Point.class, root.get("longitude"), root.get("latitude"));
                GeometryFactory factory = new GeometryFactory();
                Point comparisonPoint = factory.createPoint(new Coordinate(Double.parseDouble(longitude), Double.parseDouble(latitude))); 
                Predicate spatialPredicates = SpatialPredicates.distanceWithin(builder, point1, comparisonPoint, radius);
                predicates.add(spatialPredicates);
                
                predicates.add(builder.isNotNull(root.get("longitude")));
                predicates.add(builder.isNotNull(root.get("latitude")));
            }

            if (tagKeyword != null && !tagKeyword.isEmpty()) {
                predicates.add(builder.equal(root.get("keyword"), tagKeyword));
            }
            
            //use this if you want to group
            // query.groupBy(storeDetails.get("id"));
            // query.distinct(true);

                    
            predicates.add(QueryByExamplePredicateBuilder.getPredicate(root, builder, example));

            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };

    }

    public TagZone createTagZone(TagZone tagZone){
        
        return tagZoneRepository.save(tagZone);

    }

    public TagZone updateTagZone(Integer id, TagZone tagZoneData){

        TagZone data = tagZoneRepository.findById(id).get();
        
        return tagZoneRepository.save(TagZone.updateData(data,tagZoneData));  
        
    }

    public Boolean deleteTagZone(Integer id){

        Optional<TagZone> data = tagZoneRepository.findById(id);

        if (data.isPresent()) {

            List<TagTable> tagTableList = tagTableRepository.findByZoneId(id);
            for(TagTable t:tagTableList){
                tagTableRepository.deleteById(t.getId());
            }
            tagZoneRepository.deleteById(id);

            return true;

        } else {
            return false;
        }

    }

    public TagTable createTagTable(TagTable tagTable){
        
        return tagTableRepository.save(tagTable);

    }


    public TagTable updateTagTable(Integer id, TagTable tagTableData){

        TagTable data = tagTableRepository.findById(id).get();
        
        return tagTableRepository.save(TagTable.updateData(data,tagTableData));  
        
    }

    public Boolean deleteTagTable(Integer id){

        Optional<TagTable> data = tagTableRepository.findById(id);

        if (data.isPresent()) {

            tagTableRepository.deleteById(id);

            return true;

        } else {
            return false;
        }

    }

 
}
