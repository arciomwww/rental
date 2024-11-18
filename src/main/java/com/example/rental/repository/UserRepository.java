package com.example.rental.repository;

import com.example.rental.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findByEmail(String email);
    List<User> findByFirstName(String firstName);
    List<User> findByLastName(String lastName);
    List<User> findByEnabled(Boolean enabled);

    @Query(value = "SELECT * FROM users WHERE enabled = :enabled", nativeQuery = true)
    List<User> findUsersByEnabledStatusNative(@Param("enabled") Boolean enabled);

    @Query("SELECT u FROM User u WHERE u.firstName = :firstName AND u.lastName = :lastName")
    List<User> findUsersByFirstNameAndLastNameJPQL(@Param("firstName") String firstName, @Param("lastName") String lastName);

}
