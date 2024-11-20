package com.rutuja.employee.model;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name="employeee")
@Data
public class EmployeeModel {
    @Id
    @Column(value="empId")
    private Integer empId;

    @Column(value="fName")
    private String fName;

    @Column(value="lName")
    private String lName;

    @Column(value="sallary")
    private Float sallary;

    @Column(value="departmentId")
    private Integer departmentId;

    @Column(value="addressId")
    private Integer addressId;
}
