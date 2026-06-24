package org.example.controler;




import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UsersController {


    @Autowired
    UserRepository userRepository;




    @PostMapping()
    public User addUser(@RequestBody User user){

        System.out.println("RECEIVED USER IN PATIENT-SERVICE");
        System.out.println(user.getUsername());


        return userRepository.save(user);
    }

    @GetMapping()
    public Optional<User> findUser(@RequestParam String username){

        return userRepository.findByUsername(username);
    }

    @PutMapping()
    public User update(@RequestBody User user){

        return userRepository.save(user);
    }

    @DeleteMapping()
    public void delete(@RequestParam String username){

        userRepository.deleteByUsername(username);
    }



}
