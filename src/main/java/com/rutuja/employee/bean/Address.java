package com.rutuja.employee.bean;

import lombok.Data;

@Data
public class Address {
    private Integer addressId;
    private String  addressLine1;
    private String addressLine2;
    private StateBean state;
    private DistrictBean district;
    private CountryBean country;
}
