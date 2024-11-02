package com.rocs.ticketing.system.repository.user;

import com.rocs.ticketing.system.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Finds a user by their username.
     *
     * @param username username of the user
     * @return username
     */
    User findUserByUsername(String username);
    /**
     * Checks if a user exists with the user ID.
     *
     * @param userId the user ID
     * @return user with ID
     */
    boolean existsByUserId(String userId);
    /**
     * Checks if a user exists with username.
     *
     * @param username the username of the user
     * @return user with username
     */
    boolean existsUserByUsername(String username);
    boolean existsByUsername(String username);}
