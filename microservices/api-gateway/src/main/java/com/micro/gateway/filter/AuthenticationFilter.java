package com.micro.gateway.filter;

import com.micro.gateway.customExceptions.UserTokenNotValid;
import com.micro.gateway.util.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

//creating AuthenticationFilter custom class to add authentication on other microservice
@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator validator;
    @Autowired
    private JwtService jwtService;

    //this method is call when you pass the AuthenticationFilter call name as filter in properties
    // to specific  microservice i.e.[spring.cloud.gateway.routes[0].filters=AuthenticationFilter]
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (validator.isSecured.test(exchange.getRequest())) {
                //header contains token or not
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new RuntimeException("missing authorization header");
                }
                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }
                try {
                   //REST call to AUTH service to validate token
                   //  template.getForObject("http://AUTHENTICATION-SERVICE//validate?token" + authHeader, String.class);
                    // here we directly create same class to validate token on the basic of secret key only
                    //if you want to check in DB as well is this validate username then we need to call the auth request
                    if (!jwtService.isTokenValid(authHeader))
                        throw new RuntimeException("Token Information Is Not Valid.");

                } catch (Exception e) {
                    log.error("invalid access...!:: {}", e.getMessage());
                    throw new UserTokenNotValid();
                }
            }
            return chain.filter(exchange);
        };
    }

    public static class Config {

    }

    public AuthenticationFilter() {
        super(Config.class);
    }


}
