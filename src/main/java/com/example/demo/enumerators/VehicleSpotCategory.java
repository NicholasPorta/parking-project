package com.example.demo.enumerators;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VehicleSpotCategory {
    STANDARD("standard"),
    DISABLE("disable"),
    VIP("vip"),
    ELECTRIC("electric");


    private final String categoryName;
}
