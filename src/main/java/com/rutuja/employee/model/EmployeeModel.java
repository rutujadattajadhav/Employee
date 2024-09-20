package com.rutuja.employee.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity(name="employeee")
@Data
public class EmployeeModel {
    @Id
    @Column(name="empId")
    private Integer empId;

    @Column(name="fName")
    private String fName;

    @Column(name="lName")
    private String lName;

    @Column(name="sallary")
    private Float sallary;

    @Column(name="departmentId")
    private Integer departmentId;

    @Column(name="addressId")
    private Integer addressId;
}
