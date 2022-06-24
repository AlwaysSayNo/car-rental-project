package com.epam.nazar.grinko.services;

import com.epam.nazar.grinko.domians.Car;
import com.epam.nazar.grinko.domians.CarBrand;
import com.epam.nazar.grinko.domians.CarColor;
import com.epam.nazar.grinko.domians.helpers.CarSegment;
import com.epam.nazar.grinko.domians.helpers.CarStatus;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import java.util.*;

@Service
@AllArgsConstructor
public class CarQueryManipulationService {
    @PersistenceContext
    private EntityManager em;

    private final CarBrandService brandService;
    private final CarColorService colorService;

    private final static List<String> availableFilters = Arrays.asList("segment", "brand", "color");
    private final static List<String> availableSorts = Arrays.asList("pricePerDay", "name");

    public PageRequest createRequest(Integer page, Integer size, String sortBy, String direction){
        if(sortBy != null && !availableSorts.contains(sortBy)) throw new IllegalArgumentException();

        if(!availableSorts.contains(sortBy)) return PageRequest.of(page, size);
        return PageRequest.of(page, size, Sort.Direction.valueOf(direction), sortBy);
    }

    private List<Predicate> getPredicates(CriteriaBuilder builder, Root<Car> root, String filterBy, String filterValue){
        if(filterBy != null && !availableFilters.contains(filterBy)) throw new IllegalArgumentException();

        List<Predicate> predicates = new ArrayList<>();
        if(filterBy != null){
            switch (filterBy){
                case "segment": {
                    predicates.add(builder.equal(root.get("segment"), CarSegment.valueOf(filterValue)));
                    break;
                }
                case "brand": {
                    predicates.add(builder.equal(root.get("brand"), brandService.getBrand(filterValue)));
                    break;
                }
                case "color": {
                    predicates.add(builder.equal(root.get("color"), colorService.getColor(filterValue)));
                    break;
                }
            }
        }

        return predicates;
    }

    public Page<Car> evaluateQuery(PageRequest request, String filterBy, String filterValue,
                                    Map<String, Object> byValue){
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Car> query = builder.createQuery(Car.class);
        Root<Car> root = query.from(Car.class);

        query.select(root);

        List<Predicate> predicates = getPredicates(builder, root, filterBy, filterValue);
        if(byValue.containsKey("status")) {
            CarStatus status = (CarStatus) byValue.get("status");
            predicates.add(builder.equal(root.get("status"), status));
        }
        if(!predicates.isEmpty()) query.where(builder.and(predicates.toArray(new Predicate[0])));

        List<Order> orderList = getOrderBy(builder, root, request);
        if(!orderList.isEmpty()) query.orderBy(orderList);

        List<Car> result = em.createQuery(query)
                .setFirstResult((int) request.getOffset())
                .setMaxResults(request.getPageSize())
                .getResultList();

        Long count = countWithPredicates(builder, predicates);

        return new PageImpl<>(result, request, count);
    }

    private List<Order> getOrderBy(CriteriaBuilder builder, Root<Car> root, PageRequest request){
        List<Order> orderList = new ArrayList<>();
        Sort sort = request.getSort();
        sort.stream()
                .forEach(
                        order -> {
                            String column = order.getProperty();
                            if(order.isAscending()) orderList.add(builder.asc(root.get(column)));
                            else if(order.isDescending())orderList.add(builder.desc(root.get(column)));
                        }
                );
        return orderList;
    }

    private Long countWithPredicates(CriteriaBuilder builder, List<Predicate> predicates){
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<Car> booksRootCount = countQuery.from(Car.class);

        countQuery.select(builder.count(booksRootCount));
        if(!predicates.isEmpty()) countQuery.where(builder.and(predicates.toArray(new Predicate[0])));

        return em.createQuery(countQuery).getSingleResult();
    }

}
