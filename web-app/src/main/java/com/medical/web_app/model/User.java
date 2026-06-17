package com.medical.web_app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;



public class User {



    private Long id;
    private String username;
    private String prenom;
    private String email;
    private String password;
    private String role;

    public String getClients() {
        return clients;
    }

    public void setClients(String clients) {
        this.clients = clients;
    }

    public String getActiveClient() {
        return ActiveClient;
    }

    public void setActiveClient(String activeClient) {
        ActiveClient = activeClient;
    }

    private String clients;
    private String ActiveClient;

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
