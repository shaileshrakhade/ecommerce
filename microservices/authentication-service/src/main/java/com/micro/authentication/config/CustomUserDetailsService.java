package com.micro.authentication.config;

import com.micro.authentication.dto.UserCredential;
import com.micro.authentication.service.UserService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    @Lazy
//    we use here @Lazy because we use CustomUserDetailsService class in UserService
//     for token validation that why here cycle dependency happened so for avoid that we use @Lazy
    private UserService userService;

    //**Customizing the user details
    //load the user details in UserDetails POGO
    // those username pass by user in request we get the password from table & pass it UserDetails POGO
    // after that Spring security will take care to validate username & password pass by user
    //if the username & password is correct then it returns true else it throws the Exception
    //here we use CustomUserDetails it's child class of UserDetails we use CustomUserDetails constructor
    // to set username & password to the parent class
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<UserCredential> user = userService.getUserByEmail(email);
        return user.map(CustomUserDetails::new).orElseThrow(() -> new UsernameNotFoundException("Username or Password is incorrect..."));

    }
}
