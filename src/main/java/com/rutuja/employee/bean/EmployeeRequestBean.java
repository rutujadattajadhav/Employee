package com.rutuja.employee.bean;

import lombok.Data;

@Data
public class EmployeeRequestBean {

    private Integer empId ;
    private String firstname;
    private String lastname;
    private Float sallary ;
    private Department department;
    private Address  address;
}
