package com.epam.nazar.grinko.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractQueryManipulation <T> implements QueryManipulation <T>{

    protected EntityManager em;

    protected final List<String> availableFilters;
    protected final List<String> availableSorts;

    protected final Class<T> type;

    protected AbstractQueryManipulation(EntityManager em, List<String> availableFilters,
                                        List<String> availableSorts, Class<T> type) {
        this.em = em;
        this.availableFilters = availableFilters;
        this.availableSorts = availableSorts;
        this.type = type;
    }

    @Override
    public PageRequest createRequest(Integer page, Integer size, String sortBy, String direction) {
        if(sortBy != null && !availableSorts.contains(sortBy))
            throw new IllegalArgumentException("The argument" + sortBy + " is not in the sort list.");

        if(!availableSorts.contains(sortBy)) return PageRequest.of(page, size);
        return PageRequest.of(page, size, Sort.Direction.valueOf(direction), sortBy);
    }

    public List<Order> getOrderBy(CriteriaBuilder builder, Root<T> root, PageRequest request) {
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

    public Long countWithPredicates(CriteriaBuilder builder, List<Predicate> predicates) {
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<T> root = countQuery.from(type);

        countQuery.select(builder.count(root));
        if(!predicates.isEmpty()) countQuery.where(builder.and(predicates.toArray(new Predicate[0])));

        return em.createQuery(countQuery).getSingleResult();
    }

    public Page<T> evaluateQuery(PageRequest request, Map<String, List<String>> filter){
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(type);
        Root<T> root = query.from(type);

        query.select(root);

        List<Predicate> predicates = getPredicates(builder, root, filter);
        if(!predicates.isEmpty()) query.where(builder.and(predicates.toArray(new Predicate[0])));

        List<Order> orderList = getOrderBy(builder, root, request);
        if(!orderList.isEmpty()) query.orderBy(orderList);

        List<T> result = em.createQuery(query)
                .setFirstResult((int) request.getOffset())
                .setMaxResults(request.getPageSize())
                .getResultList();

        Long count = countWithPredicates(builder, predicates);

        return new PageImpl<>(result, request, count);
    }


    protected abstract List<Predicate> getPredicates(CriteriaBuilder builder, Root<T> root, Map<String, List<String>> filter);

}
