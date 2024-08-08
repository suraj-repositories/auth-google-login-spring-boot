package com.on08aug24.dto;

import java.sql.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserDto {
	
	private Long id;
	
	private String name;
	
	private String email;
	
	private String password;
	
	private MultipartFile avatar;
	
//	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date dob;
	
	private String role;
	
	
}
