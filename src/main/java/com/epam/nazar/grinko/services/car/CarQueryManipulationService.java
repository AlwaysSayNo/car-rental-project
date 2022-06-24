package com.epam.nazar.grinko.services.car;

import com.epam.nazar.grinko.domians.Car;
import com.epam.nazar.grinko.domians.helpers.CarSegment;
import com.epam.nazar.grinko.domians.helpers.CarStatus;
import com.epam.nazar.grinko.services.AbstractQueryManipulation;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.*;

@Service
public class CarQueryManipulationService extends AbstractQueryManipulation<Car> {

    private final CarBrandService brandService;
    private final CarColorService colorService;


    public CarQueryManipulationService(EntityManager em, CarBrandService brandService, CarColorService colorService){
        super(em, Arrays.asList("segment", "brand", "color", "status"), Arrays.asList("pricePerDay", "name"), Car.class);
        this.brandService = brandService;
        this.colorService = colorService;
    }

    @Override
    protected List<Predicate> getPredicates(CriteriaBuilder builder, Root<Car> root, Map<String, String> filter){
        List<Predicate> predicates = new ArrayList<>();

        if(!filter.isEmpty()) {
            filter.forEach((filterBy, value) -> {

                switch (filterBy) {
                    case "segment":
                        predicates.add(builder.equal(root.get("segment"), CarSegment.valueOf(value)));
                        break;
                    case "brand":
                        predicates.add(builder.equal(root.get("brand"), brandService.getBrand(value)));
                        break;
                    case "color":
                        predicates.add(builder.equal(root.get("color"), colorService.getColor(value)));
                        break;
                    case "status":
                        predicates.add(builder.equal(root.get("status"), CarStatus.valueOf(value)));
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
            });
        }

        return predicates;
    }

}
