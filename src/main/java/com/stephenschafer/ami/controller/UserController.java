package com.stephenschafer.ami.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stephenschafer.ami.jpa.UserEntity;
import com.stephenschafer.ami.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/users")
public class UserController {
	@Autowired
	private UserService userService;

	@PostMapping
	public ApiResponse<UserEntity> saveUser(@RequestBody final UserEntity user) {
		log.info("POST /users " + user);
		UserEntity userEntity;
		try {
			userEntity = userService.save(user);
		}
		catch (final Exception e) {
			log.error("Failed to insert user", e);
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"Failed to insert user: " + e.getMessage(), null);
		}
		return new ApiResponse<>(HttpStatus.OK.value(), "User saved successfully.", userEntity);
	}

	@PutMapping("/{id}")
	public ApiResponse<UserEntity> update(@RequestBody final UserEntity userDto) {
		log.info("PUT /users " + userDto);
		UserEntity userEntity;
		try {
			userEntity = userService.update(userDto);
		}
		catch (final Exception e) {
			log.error("Failed to update user", e);
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"Failed to update user: " + e.getMessage(), null);
		}
		return new ApiResponse<>(HttpStatus.OK.value(), "User updated successfully.", userEntity);
	}

	@GetMapping
	public ApiResponse<List<UserEntity>> listUser() {
		return new ApiResponse<>(HttpStatus.OK.value(), "User list fetched successfully.",
				userService.findAll());
	}

	@GetMapping("/{id}")
	public ApiResponse<UserEntity> getOne(@PathVariable final int id) {
		return new ApiResponse<>(HttpStatus.OK.value(), "User fetched successfully.",
				userService.findById(id));
	}

	@DeleteMapping("/{id}")
	public ApiResponse<Void> delete(@PathVariable final int id) {
		log.info("DELETE /users/" + id);
		try {
			userService.delete(id);
		}
		catch (final Exception e) {
			log.error("Failed to delete user", e);
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"Failed to delete user: " + e.getMessage(), null);
		}
		return new ApiResponse<>(HttpStatus.OK.value(), "User deleted successfully.", null);
	}
}
