package com.micro.authentication.controller;

import com.micro.authentication.customeExceptions.InvalidTokenCustomeException;
import com.micro.authentication.customeExceptions.UsernameAlreadyExistCustomeException;
import com.micro.authentication.dto.UserCredential;
import com.micro.authentication.dto.UserDetailsDto;
import com.micro.authentication.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("register")
    public String createUser(@RequestBody UserDetailsDto userDetails) throws UsernameAlreadyExistCustomeException {
     // if the emailed already exist then throw the exception
        userService.isUserExist(userDetails.getEmail());
        String username = userService.createUser(userDetails).getEmail();
        return "User register successfully with username :: " + username;
    }

    @PostMapping("token")
    public String getGenerate(@RequestBody UserCredential userCredential) throws UsernameNotFoundException {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(userCredential.getUsername(), userCredential.getPassword()));
            if (authentication.isAuthenticated())
                return userService.generateToken(userCredential.getUsername());

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new UsernameNotFoundException("");
        }

        return "";
    }

    @GetMapping("validate")
    public boolean validateToken(@RequestParam("token") String token) throws InvalidTokenCustomeException {
        try {
            return userService.validateToken(token);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new InvalidTokenCustomeException();
        }

    }
}
