package com.genhub;

import java.util.TimeZone;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class GenHubApplication implements ApplicationRunner {


	public static void main(String[] args) {
		SpringApplication.run(GenHubApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		init();
		System.setProperty(
				"org.apache.commons.logging.simplelog.log.org.apache.http.client.protocol.ResponseProcessCookies",
				"fatal");
	}

	void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

//		if (!roleRepo.findByName(RoleName.ROLE_ADMIN).isPresent()) {
//			Role role_admin = new Role(RoleName.ROLE_ADMIN);
//			roleRepo.save(role_admin);
//		}
//
//		if (!roleRepo.findByName(RoleName.ROLE_USER).isPresent()) {
//			Role role_user = new Role(RoleName.ROLE_USER);
//			roleRepo.save(role_user);
//		}

	}

}
