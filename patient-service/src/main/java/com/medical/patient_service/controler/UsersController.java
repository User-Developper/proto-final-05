package com.medical.patient_service.controler;


import com.medical.patient_service.model.User;
import com.medical.patient_service.service.UserService;
import com.medical.patient_service.service.form.DataServiceFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UsersController {


    @Autowired
    UserService userService;

    @Autowired
    DataServiceFeignClient dataServiceFeignClient;



    @PostMapping()
    public User addUser(@RequestBody User user){

        System.out.println("🔥 RECEIVED USER IN PATIENT-SERVICE");
        System.out.println(user.getUsername());


        return dataServiceFeignClient.addUser(user);
    }

    @GetMapping()
    public Optional<User> findUser(@RequestParam  String username){

        return dataServiceFeignClient.findUser(username);
    }

//    @PutMapping()
//    public User update(@RequestBody User user){
//
//        return dataServiceFeignClient.updateUser(user);
//    }
//
//    @DeleteMapping()
//    public void delete(@RequestParam String username){
//
//         dataServiceFeignClient.deleteUser(username);
//    }






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
