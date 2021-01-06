package com.arraisi.antrianrumahsakit.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueueNumbers {
    private Integer totalQueue;
    private Integer currentQueue;
    private Integer pharmacyQueue;
    private Integer doneQueue;
}
