package com.example.vishnu.Nectar.service;

import com.example.vishnu.Nectar.dto.UserRequest;
import com.example.vishnu.Nectar.dto.UserResponse;
import com.example.vishnu.Nectar.entity.User;
import com.example.vishnu.Nectar.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    public String createUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "User added successfully";
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponse> userResponses = users.stream().map(
                (user)->{
                    UserResponse userResponse = new UserResponse();
                    userResponse.setId(user.getId());
                    userResponse.setName(user.getName());
                    userResponse.setEmail(user.getEmail());
                    userResponse.setPhone(user.getPhone());
                    userResponse.setRoles(user.getRoles());
                    return userResponse;
                }).toList();
        return userResponses;
    }

    public String updateUser(UserRequest userRequest) {
        Optional<User> existingUser = userRepository.findById(userRequest.getId());
        if(existingUser.isPresent()){
            User user = existingUser.get();
            user.setName(userRequest.getName());
            user.setEmail(userRequest.getEmail());
            user.setPhone(userRequest.getPhone());
            user.setRoles(userRequest.getRoles());
            userRepository.save(user);
            return "User updated successfully";
        }else{
            System.out.println("user not found");
            return "No user found";
        }

    }

    public String deleteUser(int id){
        Optional<User> existingUser = userRepository.findById(id);
        if(existingUser.isPresent()){
            userRepository.deleteById(id);
            return "User deleted successfully";
        }else{
            System.out.println("user not found");
            return "No user found";
        }

    }
}