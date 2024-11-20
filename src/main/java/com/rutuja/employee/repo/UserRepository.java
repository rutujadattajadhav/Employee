package com.rutuja.employee.repo;


import com.rutuja.employee.entity.UserEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends ReactiveCrudRepository<com.rutuja.employee.entity.UserEntity,String> {
}
