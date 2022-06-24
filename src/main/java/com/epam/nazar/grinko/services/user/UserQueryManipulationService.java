package com.epam.nazar.grinko.services.user;

import com.epam.nazar.grinko.domians.User;
import com.epam.nazar.grinko.domians.helpers.UserRole;
import com.epam.nazar.grinko.domians.helpers.UserStatus;
import com.epam.nazar.grinko.services.AbstractQueryManipulation;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class UserQueryManipulationService extends AbstractQueryManipulation<User> {

    protected UserQueryManipulationService(EntityManager em) {
        super(em, Arrays.asList("status", "role"), Arrays.asList("email", "firstName", "lastName"), User.class);
    }

    @Override
    protected List<Predicate> getPredicates(CriteriaBuilder builder, Root<User> root, Map<String, String> filter) {
        List<Predicate> predicates = new ArrayList<>();

        filter.forEach((filterBy, value) -> {
            switch (filterBy) {
                case "status":
                    predicates.add(builder.equal(root.get("status"), UserStatus.valueOf(value)));
                    break;
                case "role":
                    predicates.add(builder.equal(root.get("role"), UserRole.valueOf(value)));
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        });

        return predicates;
    }

}
