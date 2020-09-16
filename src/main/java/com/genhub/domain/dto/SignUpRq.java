package com.genhub.domain.dto;

import javax.validation.constraints.*;

import lombok.Data;

@Data
public class SignUpRq {
	@NotBlank
	@Size(min = 4, max = 40)
	private String name;

	@NotBlank
	@Size(max = 40)
	@Email
	private String email;

	@NotBlank
	@Size(min = 6, max = 20)
	private String password;

}
