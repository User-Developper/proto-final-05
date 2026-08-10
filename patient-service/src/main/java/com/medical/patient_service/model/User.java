package com.medical.patient_service.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String prenom;
    private String email;
    private String password;
    private String role;

    @ManyToMany
    private List<ProfessionalClient> clients = new ArrayList<>();

    @ManyToOne
    private ProfessionalClient activeClient;

}
