package com.kalsym.locationservice.service;

import java.util.ArrayList;
import java.util.List;

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
import com.kalsym.locationservice.repository.TagKeywordRepository;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Coordinate;
import org.hibernate.spatial.predicate.SpatialPredicates;

@Service

public class TagKeywordService {

    @Autowired
    TagKeywordRepository tagKeywordRepository;

    public List<TagKeyword> getTagList(){

        List<TagKeyword> result = tagKeywordRepository.findAll();
        
        return result;
        
    }

    public Page<TagKeyword> getTagListWithPageable(
        int page, int pageSize, String latitude,String longitude, double searchRadius,String sortByCol, Sort.Direction sortingOrder
    ){
        Pageable pageable;

        TagKeyword tagKeywordMatch = new TagKeyword();

        ExampleMatcher matcher = ExampleMatcher
        .matchingAll()
        .withIgnoreCase()
        .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
        Example<TagKeyword> example = Example.of(tagKeywordMatch, matcher);

        if (sortingOrder==Sort.Direction.ASC){
            pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).ascending());
        } 
        else{
            pageable = PageRequest.of(page, pageSize, Sort.by(sortByCol).descending());
        } 
        Specification<TagKeyword> tagKeywordSpecs = searchStoreCategorySpecs(latitude, longitude,searchRadius,example);

        Page<TagKeyword> result = tagKeywordRepository.findAll(tagKeywordSpecs,pageable);       

        return result;
          
    }
    

    public static Specification<TagKeyword> searchStoreCategorySpecs(
            String latitude, 
            String longitude,
            double radius,        
            Example<TagKeyword> example) {
    
        return (Specification<TagKeyword>) (root, query, builder) -> {
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
            
            //use this if you want to group
            // query.groupBy(storeDetails.get("id"));
            // query.distinct(true);

                    
            predicates.add(QueryByExamplePredicateBuilder.getPredicate(root, builder, example));

            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };

    }
}
