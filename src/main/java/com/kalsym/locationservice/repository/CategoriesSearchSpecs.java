package com.kalsym.locationservice.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.domain.Specification;

import com.kalsym.locationservice.model.StoreCategory;
import com.kalsym.locationservice.model.Config.StoreConfig;

import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;

public class CategoriesSearchSpecs {
     /**
     * Accept two dates and example matcher
     *
     * @param parentCategoryId     
     * @param example
     * @return
     */
    public static Specification<StoreConfig> getSpecWithDatesBetween(
            String parentCategoryId,
            Example<StoreConfig> example) {

        return (Specification<StoreConfig>) (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            Join<StoreConfig, StoreCategory> category = root.join("categories");
            
            if (parentCategoryId!=null) {
                predicates.add(builder.equal(category.get("parentCategoryId"), parentCategoryId));
            } 
            
            predicates.add(QueryByExamplePredicateBuilder.getPredicate(root, builder, example));

            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
