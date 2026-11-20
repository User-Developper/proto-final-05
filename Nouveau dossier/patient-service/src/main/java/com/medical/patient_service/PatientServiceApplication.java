package com.medical.patient_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class PatientServiceApplication {

	public static void main(String[] args) {
		System.out.println("===== JE DEMARRE LE PATIENT SERVICE =====") ;SpringApplication.run(PatientServiceApplication.class, args);
	}

}
