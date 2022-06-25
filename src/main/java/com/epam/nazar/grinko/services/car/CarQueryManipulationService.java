package com.epam.nazar.grinko.services.car;

import com.epam.nazar.grinko.domians.Car;
import com.epam.nazar.grinko.domians.CarBrand;
import com.epam.nazar.grinko.domians.CarColor;
import com.epam.nazar.grinko.domians.helpers.CarSegment;
import com.epam.nazar.grinko.domians.helpers.CarStatus;
import com.epam.nazar.grinko.services.AbstractQueryManipulation;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CarQueryManipulationService extends AbstractQueryManipulation<Car> {

    private final CarBrandService brandService;
    private final CarColorService colorService;


    public CarQueryManipulationService(EntityManager em, CarBrandService brandService, CarColorService colorService){
        super(em, Arrays.asList("segment", "brand", "color", "status"),
                Arrays.asList("pricePerDay", "name"), Car.class);
        this.brandService = brandService;
        this.colorService = colorService;
    }

    @Override
    protected List<Predicate> getPredicates(CriteriaBuilder builder, Root<Car> root, Map<String, List<String>> filter){
        List<Predicate> predicates = new ArrayList<>();

        if(!filter.isEmpty()) {
            filter.forEach((filterBy, value) -> {

                switch (filterBy) {
                    case "segment":
                        List<CarSegment> segments = value.stream()
                                .map(CarSegment::valueOf)
                                .collect(Collectors.toList());
                        predicates.add(root.get("segment").in(segments));
                        break;
                    case "brand":
                        List<CarBrand> brands = value.stream()
                                .map(brandService::getBrand)
                                .collect(Collectors.toList());
                        predicates.add(root.get("brand").in(brands));
                        break;
                    case "color":
                        List<CarColor> colors = value.stream()
                                .map(colorService::getColor)
                                .collect(Collectors.toList());
                        predicates.add(root.get("color").in(colors));
                        break;
                    case "status":
                        List<CarStatus> statuses = value.stream()
                                .map(CarStatus::valueOf)
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

}
