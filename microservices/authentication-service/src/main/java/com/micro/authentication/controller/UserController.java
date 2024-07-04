package com.micro.authentication.controller;

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
    public String createUser(@RequestBody UserDetailsDto userDetails) {
        try {
            Long id = 0L;
            id = userService.createUser(userDetails);
            if (id > 0)
                return "User register successfully.";
            else throw new RuntimeException("User Not Register try again latter.");
        } catch (Exception e) {
            log.error("Having Trouble with Registration :: {}", e.getMessage());
            return new RuntimeException("Having Trouble try again after some time").getMessage();
        }
    }

    @PostMapping("token")
    public String getGenerate(@RequestBody UserCredential userCredential) {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(userCredential.getUsername(), userCredential.getPassword()));
            if (authentication.isAuthenticated()) {
                return userService.generateToken(userCredential.getUsername());
            } else {
                throw new UsernameNotFoundException("User Not Found");
            }
        } catch (Exception e) {
            log.error("User Not valid :: {}", e.getMessage());
            return e.getMessage();
        }
    }

    @GetMapping("validate")
    public boolean validateToken(@RequestParam("token") String token) {
        try {
            return userService.validateToken(token);
        } catch (Exception e) {
            log.error("Token is not valid :: {}", e.getMessage());
            return false;
        }
    }
}
