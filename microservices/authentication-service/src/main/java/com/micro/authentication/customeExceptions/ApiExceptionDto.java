package com.micro.authentication.customeExceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Data
public class ApiExceptionDto {
    private HttpStatus httpStatus;
    private String errorMessage;
    private Date date=new Date();

    public ApiExceptionDto(HttpStatus httpStatus, String errorMessage) {
        this.httpStatus=httpStatus;
        this.errorMessage=errorMessage;
    }
}
