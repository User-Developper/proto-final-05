package org.example.controler;


import com.medical.patient_service.model.User;
import com.medical.patient_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

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






//    @PostMapping()
//    public String submitReportStep1(@RequestBody Report report){
//
//
//
//        System.out.println(reportForm.getCategory());
//        System.out.println(reportForm.getTitle());
//        System.out.println(reportForm.getDescription());
//        System.out.println(reportForm.getGravity());
//        System.out.println(reportForm.getLocation());
//        System.out.println(reportForm.getLatitude());
//        System.out.println(reportForm.getLongitude());
//
//
//    }



}
