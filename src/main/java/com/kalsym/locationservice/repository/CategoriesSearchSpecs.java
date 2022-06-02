package com.kalsym.locationservice.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.domain.Specification;

import com.kalsym.locationservice.model.Category;
import com.kalsym.locationservice.model.Config.StoreConfig;

import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;
/**
 *
 * @author taufik
 */
public class CategoriesSearchSpecs {
     /**
     * Accept two dates and example matcher
     *
     * @param currentDate     
     * @param voucherType
     * @param storeId
     * @param verticalCode
     * @param example
     * @return
     */
    public static Specification<StoreConfig> getSpecWithDatesBetween(
            String parentCategoryId,
            Example<StoreConfig> example) {

        return (Specification<StoreConfig>) (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            Join<StoreConfig, Category> category = root.join("categories");
            
            if (parentCategoryId!=null) {
                predicates.add(builder.equal(category.get("parentCategoryId"), parentCategoryId));
            } 
            
            predicates.add(QueryByExamplePredicateBuilder.getPredicate(root, builder, example));

            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
