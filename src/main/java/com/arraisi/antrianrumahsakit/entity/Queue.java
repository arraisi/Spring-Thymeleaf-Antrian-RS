package com.arraisi.antrianrumahsakit.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Queue {

    private Integer id;
    private Patient patientId;
    private Integer queueNumber;
    private String status;

}
