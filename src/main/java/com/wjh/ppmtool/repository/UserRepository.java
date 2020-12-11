package com.wjh.ppmtool.repository;

import com.wjh.ppmtool.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String Username);
    User getById(Long id);

    Optional<User> findById(Long id);
}
