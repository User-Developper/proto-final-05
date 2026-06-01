package com.medical.patient_service.service;


import com.medical.patient_service.model.User;
import com.medical.patient_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {



    @Autowired
    UserRepository userRepository;



    public User addUser(User user){

        return userRepository.save(user);
    }

    public Optional<User> findUser(String username){

        return userRepository.findByUsername(username);
    }

    public User updateUser(User user){

        return userRepository.save(user);
    }

    public void deleteUser(String username){

        userRepository.deleteByUsername(username);
    }
}
