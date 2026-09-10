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


    // =========================================================
    // AJOUTER UN UTILISATEUR
    // =========================================================

    @PostMapping()
    public User addUser(@RequestBody User user) {

        System.out.println(
                "RECEIVED USER IN PATIENT-SERVICE"
        );

        System.out.println(
                user.getUsername()
        );

        return userRepository.save(user);
    }


    // =========================================================
    // RECHERCHER PAR USERNAME
    // =========================================================

    @GetMapping()
    public Optional<User> findUser(
            @RequestParam String username) {

        return userRepository.findByUsername(username);
    }


    // =========================================================
    // RECHERCHER PAR ID
    // =========================================================

    @GetMapping("/{userId}")
    public Optional<User> getUserById(
            @PathVariable Long userId) {

        return userRepository.findById(userId);
    }


    // =========================================================
    // MODIFIER
    // =========================================================

    @PutMapping()
    public User update(
            @RequestBody User user) {

        return userRepository.save(user);
    }


    // =========================================================
    // SUPPRIMER
    // =========================================================

    @DeleteMapping()
    public void delete(
            @RequestParam String username) {

        userRepository.deleteByUsername(username);
    }
}