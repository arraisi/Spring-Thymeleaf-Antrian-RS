package com.arraisi.antrianrumahsakit.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Patient {
    private Integer id;
    private String name;
    private String phoneNumber;
}
