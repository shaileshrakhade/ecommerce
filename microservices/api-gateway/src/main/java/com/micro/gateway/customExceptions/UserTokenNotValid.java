package com.micro.gateway.customExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.server.ResponseStatusException;

public class UserTokenNotValid extends ResponseStatusException {


    public UserTokenNotValid() {
        super(HttpStatus.FORBIDDEN, "Invalid Token", null);

    }

}
