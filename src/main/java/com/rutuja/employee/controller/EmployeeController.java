package com.rutuja.employee.controller;

import com.rutuja.employee.bean.EmployeeRequestBean;
import com.rutuja.employee.bean.EmployeeResponce;
import com.rutuja.employee.model.EmployeeModel;
import com.rutuja.employee.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@RestController
public class EmployeeController {
    @Autowired
     private EmployeeService employeeService;
    @Value("${xyz}")
    private String name;

    @GetMapping("/{empId}")
    public Mono<EmployeeResponce> getEmployeeById(@PathVariable("empId") Integer empId) throws Exception {
       return employeeService.getEmployeeById(empId);
    }

    @GetMapping("/getAllEmployee")
    public Flux<EmployeeResponce> getAllEmployee() throws Exception {
        return employeeService.getAllEmployee();
    }

    @GetMapping("/getEmployee")
    public Flux <EmployeeResponce> getEmployees(@RequestBody List<Integer> empIds) throws Exception {
        return employeeService.getEmployee(empIds);
    }

    @PostMapping("/saveemployee")
    public Mono<String> saveEmployee(@RequestBody EmployeeRequestBean employeeRequestBean) {
        return employeeService.saveEmployee(employeeRequestBean);
    }

    @PutMapping("/updateEmployee")
    public Mono<String> updateEmployee(@RequestBody EmployeeRequestBean employeeRequestBean) {
        return employeeService.updateEmployee(employeeRequestBean);
    }

    @DeleteMapping("/deleteEmployee/{deleteEmployeeById}")
    public Mono<String> deleteEmployee(@PathVariable("deleteEmployeeById") Integer empId) {
        return employeeService.deleteEmployee(empId);
    }
}
