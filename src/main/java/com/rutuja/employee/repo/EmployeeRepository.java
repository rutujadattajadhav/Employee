package com.rutuja.employee.repo;

import com.rutuja.employee.model.EmployeeModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends CrudRepository<EmployeeModel,Integer> {

}
