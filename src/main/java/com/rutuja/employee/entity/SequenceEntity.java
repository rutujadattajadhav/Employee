package com.rutuja.employee.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name="sequence")
public class SequenceEntity {

   @Column(value="name")
   @Id
   String name;

    @Column (value="value")
   String value;

}
