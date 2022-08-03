//package com.sky.tempest_server;
//
//import com.sky.tempest_server.user.repositories.UserRepository;
//import com.sky.tempest_server.user.entities.User;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.annotation.Bean;
//
//@SpringBootApplication
//public class TempestServerApplication {
//	@Autowired
//	private UserRepository user_repository;
//
//	public static void main(String[] args) {
//		SpringApplication.run(TempestServerApplication.class, args);
//	}
//
//	@Bean
//	CommandLineRunner runner(){
//		return args -> {
//			user_repository.save(new User("admin@sky.uk","Jane","Doe",
//					"$2a$10$LmptYa4uWhkO/OO.lpmahO14w0WKBzyKQOAXeSrYal04KKt.OTcRu",
//					"ADMIN"));
//			user_repository.save(new User("user@sky.uk","John","Smith",
//					"$2a$10$6CJhx.freP/GXr56EItYJeVc2r9P3o/ns/adeKBAuf8d65Y8qz2qi",
//					"USER"));
//		};
//	}
//}
