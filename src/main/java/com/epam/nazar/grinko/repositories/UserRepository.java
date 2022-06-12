package com.epam.nazar.grinko.repositories;

import com.epam.nazar.grinko.domians.User;
import com.epam.nazar.grinko.domians.helpers.UserStatus;
import com.epam.nazar.grinko.securities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    String UPDATE_USER_BY_ID = "UPDATE User u SET u.email=:email, u.firstName=:firstName, u.lastName=:lastName, u.password=:password, u.role=:role, u.phoneNumber=:phoneNumber, u.status=:status WHERE u.id=:id";
    String UPDATE_USER_STATUS_BY_ID = "UPDATE User u SET u.status=:status WHERE u.id=:id";
    
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByIdAndRole(long id, UserRole role);
    List<User> getAllByRole(UserRole role);

    @Transactional
    @Modifying
    @Query(UPDATE_USER_BY_ID)
    void updateUserById(@Param("email") String email,
                       @Param("firstName") String firstName,
                       @Param("lastName") String lastName,
                       @Param("password") String password,
                       @Param("role") UserRole role,
                       @Param("phoneNumber") String phoneNumber,
                       @Param("status") UserStatus status,
                       @Param("id") long id);

    @Transactional
    @Modifying
    @Query(UPDATE_USER_STATUS_BY_ID)
    void updateUserStatusById(
                        @Param("status") UserStatus status,
                        @Param("id") long id);
}
