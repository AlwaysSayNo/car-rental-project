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
import java.util.stream.Collectors;

@Service
public class UserQueryManipulationService extends AbstractQueryManipulation<User> {

    protected UserQueryManipulationService(EntityManager em) {
        super(em, Arrays.asList("status", "role"),
                Arrays.asList("email", "firstName", "lastName"), User.class);
    }

    @Override
    protected List<Predicate> getPredicates(CriteriaBuilder builder, Root<User> root, Map<String, List<String>> filter) {
        List<Predicate> predicates = new ArrayList<>();

        filter.forEach((filterBy, value) -> {
            switch (filterBy) {
                case "status":
                    List<UserStatus> statuses = value.stream()
                            .map(UserStatus::valueOf)
                            .collect(Collectors.toList());
                    predicates.add(root.get("status").in(statuses));
                    break;
                case "role":
                    List<UserRole> roles = value.stream()
                            .map(UserRole::valueOf)
                            .collect(Collectors.toList());
                    predicates.add(root.get("role").in(roles));
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        });

        return predicates;
    }

}
