package com.rutuja.employee.bean;

import lombok.Data;

@Data
public class EmployeeResponce {

    private Integer empId ;
   private String fName;
   private String lName;
    private Float sallary ;
   private Department department;
    private Address  address;
}
