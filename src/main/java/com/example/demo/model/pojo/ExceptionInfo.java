package com.example.demo.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExceptionInfo {

    private String message;
    private String exceptionClass;
    private String details;
    private String date;

}
