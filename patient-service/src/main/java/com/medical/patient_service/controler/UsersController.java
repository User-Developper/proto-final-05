package com.medical.patient_service.controler;


import com.medical.patient_service.model.User;
import com.medical.patient_service.repository.UserRepository;
import com.medical.patient_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UsersController {


    @Autowired
    UserService userService;



    @PostMapping()
    public User addUser(@RequestBody User user){

        System.out.println("🔥 RECEIVED USER IN PATIENT-SERVICE");
        System.out.println(user.getUsername());


        return userService.addUser(user);
    }

    @GetMapping()
    public Optional<User> findUser(@RequestParam  String username){

        return userService.findUser(username);
    }

    @PutMapping()
    public User update(@RequestBody User user){

        return userService.updateUser(user);
    }

    @DeleteMapping()
    public void delete(@RequestParam String username){

         userService.deleteUser(username);
    }



}
