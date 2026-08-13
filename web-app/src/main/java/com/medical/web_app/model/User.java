package com.medical.web_app.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;


public class User {



    private Long id;
    private String username;
    private String prenom;
    private String email;
    private String password;
    private String role;

    public List<ProfessionalClient> getClients() {
        return clients;
    }

    public void setClients(List<ProfessionalClient> clients) {
        this.clients = clients;
    }

    public ProfessionalClient getActiveClient() {
        return activeClient;
    }

    public void setActiveClient(ProfessionalClient activeClient) {
        this.activeClient = activeClient;
    }

    @ManyToMany
    private List<ProfessionalClient> clients = new ArrayList<>();

    @ManyToOne
    private ProfessionalClient activeClient;

//    private String clients;
//    private String ActiveClient;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }




    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
