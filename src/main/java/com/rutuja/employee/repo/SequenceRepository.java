package com.rutuja.employee.repo;

import com.rutuja.employee.entity.SequenceEntity;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface SequenceRepository extends ReactiveCrudRepository<SequenceEntity, Integer> {


    @Query(value="select value from sequence where name= 'employee'")
    public Mono<Integer> selectValue();


     @Modifying
    @Query(value="UPDATE sequence SET value =:sequecnceValue  WHERE name = 'employee'")
    public Mono<Integer> updateSequenceemployee(Integer sequecnceValue  );

}
