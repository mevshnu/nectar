package com.example.vishnu.Nectar.controller;

import com.example.vishnu.Nectar.dto.AuthRequest;
import com.example.vishnu.Nectar.dto.UserRequest;
import com.example.vishnu.Nectar.dto.UserResponse;
import com.example.vishnu.Nectar.entity.User;
import com.example.vishnu.Nectar.service.JwtService;
import com.example.vishnu.Nectar.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserService userService;


    @PostMapping("/signup")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MANAGER')")
    public String createUser(@ApiParam(value = "User object", required = true) @RequestBody User user){
        return userService.createUser(user);
    }

    @PostMapping("/login")
    public HashMap<String, String> loginUser(@ApiParam(value = "User credentials", required = true) @RequestBody AuthRequest authRequest){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(),authRequest.getPassword()));
        if(authentication.isAuthenticated()){
            HashMap<String, String> response = new HashMap<>();
            response.put("token",jwtService.generateToken(authRequest.getUsername()));
            response.put("status", "success");
            response.put("username",authRequest.getUsername());
            return response;
        }else{
            throw new UsernameNotFoundException("Invalid user request");
        }
    }

    @ApiOperation("Get all users")
    @GetMapping("/getUser")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MANAGER')")
    public List<UserResponse> getAllUsers(){
        return userService.getAllUsers();
    }

    @PutMapping("/updateUser")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MANAGER')")
    public String updateUser(@ApiParam(value = "Updated user data", required = true) @RequestBody UserRequest userRequest) {
        return userService.updateUser(userRequest);
    }

    @DeleteMapping("/deleteUser/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MANAGER')")
    public String deleteUser(@ApiParam(value = "User Id", required = true) @RequestParam("id") int id) {
        return userService.deleteUser(id);
    }
}