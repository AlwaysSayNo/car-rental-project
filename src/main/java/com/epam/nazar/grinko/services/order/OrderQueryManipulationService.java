package com.epam.nazar.grinko.services.order;

import com.epam.nazar.grinko.domians.Car;
import com.epam.nazar.grinko.domians.CarBrand;
import com.epam.nazar.grinko.domians.CarColor;
import com.epam.nazar.grinko.domians.Order;
import com.epam.nazar.grinko.domians.helpers.CarSegment;
import com.epam.nazar.grinko.domians.helpers.OrderStatus;
import com.epam.nazar.grinko.services.AbstractQueryManipulation;
import com.epam.nazar.grinko.services.car.CarBrandService;
import com.epam.nazar.grinko.services.car.CarColorService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderQueryManipulationService extends AbstractQueryManipulation<Order> {

    private final CarBrandService brandService;
    private final CarColorService colorService;

    public OrderQueryManipulationService(EntityManager em, CarBrandService brandService, CarColorService colorService){
        super(em, Arrays.asList("segment", "brand", "status"),
                Arrays.asList("user_firstName", "user_email", "car_name"), Order.class);
        this.brandService = brandService;
        this.colorService = colorService;
    }

    @Override
    protected List<Predicate> getPredicates(CriteriaBuilder builder, Root<Order> root, Map<String, List<String>> filter) {
        List<Predicate> predicates = new ArrayList<>();

        Join<Order, Car> carJoin = root.join("car");

        if(!filter.isEmpty()) {
            filter.forEach((filterBy, value) -> {

                switch (filterBy) {
                    case "segment":
                        List<CarSegment> segments = value.stream()
                                .map(CarSegment::valueOf)
                                .collect(Collectors.toList());
                        predicates.add(carJoin.get("segment").in(segments));
                        break;
                    case "brand":
                        List<CarBrand> brands = value.stream()
                                .map(brandService::getBrand)
                                .collect(Collectors.toList());
                        predicates.add(carJoin.get("brand").in(brands));
                        break;
                    case "color":
                        List<CarColor> colors = value.stream()
                                .map(colorService::getColor)
                                .collect(Collectors.toList());
                        predicates.add(carJoin.get("color").in(colors));
                        break;
                    case "status":
                        List<OrderStatus> statuses = value.stream()
                                .map(OrderStatus::valueOf)
                                .collect(Collectors.toList());
                        predicates.add(root.get("status").in(statuses));
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
            });
        }

        return predicates;
    }

    public Long countWithPredicates(CriteriaBuilder builder, List<Predicate> predicates) {
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<Order> root = countQuery.from(Order.class);

        Join<Order, Car> carJoin = root.join("car");

        countQuery.select(builder.count(root));
        if(!predicates.isEmpty()) countQuery.where(builder.and(predicates.toArray(new Predicate[0])));

        return em.createQuery(countQuery).getSingleResult();
    }

    @Override
    public List<javax.persistence.criteria.Order> getOrderBy(CriteriaBuilder builder, Root<Order> root, PageRequest request) {
        List<javax.persistence.criteria.Order> orderList = new ArrayList<>();
        Sort sort = request.getSort();
        sort.stream()
                .forEach(
                        order -> {
                            String column = order.getProperty();
                            Join<Order, ?> join;

                            if(column.startsWith("user_")){
                                column = column.replace("user_", "");
                                join = root.join("user");
                            } else if(column.startsWith("car_")){
                                column = column.replace("car_", "");
                                join = root.join("car");
                            } else{
                                throw new IllegalArgumentException();
                            }


                            if(order.isAscending()) orderList.add(builder.asc(join.get(column)));
                            else if(order.isDescending())orderList.add(builder.desc(join.get(column)));
                        }
                );
        return orderList;
    }

}
