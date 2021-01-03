package com.stephenschafer.ami.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.stephenschafer.ami.jpa.UserEntity;
import com.stephenschafer.ami.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class UserController {
	@Autowired
	private UserService userService;

	@PostMapping("/user")
	public ApiResponse<UserEntity> saveUser(@RequestBody final UserEntity user) {
		log.info("POST /user " + user);
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

	@PutMapping("/user")
	public ApiResponse<UserEntity> update(@RequestBody final UserEntity user) {
		log.info("PUT /user " + user);
		UserEntity userEntity;
		try {
			userEntity = userService.update(user);
		}
		catch (final Exception e) {
			log.error("Failed to update user", e);
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"Failed to update user: " + e.getMessage(), null);
		}
		return new ApiResponse<>(HttpStatus.OK.value(), "User updated successfully.", userEntity);
	}

	@PutMapping("/user/password")
	public ApiResponse<Void> changePassword(@RequestBody final Map<String, String> map,
			final HttpServletRequest request) {
		log.info("PUT /user/password " + map);
		final String username = (String) request.getAttribute("username");
		final UserEntity user = userService.findByUsername(username);
		userService.changePassword(user.getId(), map.get("password"));
		return new ApiResponse<>(HttpStatus.OK.value(), "Password changed successfully.", null);
	}

	@PutMapping("/user/password/{id}")
	public ApiResponse<Void> changePassword(@PathVariable final int id,
			@RequestBody final Map<String, String> map, final HttpServletRequest request) {
		log.info("PUT /user/password/" + id + ", " + map);
		final UserEntity user = userService.findById(id);
		userService.changePassword(user.getId(), map.get("password"));
		return new ApiResponse<>(HttpStatus.OK.value(), "Password changed successfully.", null);
	}

	@GetMapping("/users")
	public ApiResponse<List<UserEntity>> listUser() {
		return new ApiResponse<>(HttpStatus.OK.value(), "User list fetched successfully.",
				userService.findAll());
	}

	@GetMapping("/user/{id}")
	public ApiResponse<UserEntity> getOne(@PathVariable final int id) {
		log.info("GET /user/" + id);
		return new ApiResponse<>(HttpStatus.OK.value(), "User fetched successfully.",
				userService.findById(id));
	}

	@GetMapping("/user/context")
	public ApiResponse<Map<String, String>> getContext(final HttpServletRequest request) {
		log.info("GET /user/context");
		final String username = (String) request.getAttribute("username");
		final UserEntity user = userService.findByUsername(username);
		final Map<String, String> map = new HashMap<>();
		map.put("context", user.getContext());
		return new ApiResponse<>(HttpStatus.OK.value(), "User context fetched successfully.", map);
	}

	@PutMapping("/user/context")
	public ApiResponse<Void> setContext(@RequestBody final Map<String, String> map,
			final HttpServletRequest request) {
		log.info("PUT /user/context " + map);
		final String username = (String) request.getAttribute("username");
		final UserEntity user = userService.findByUsername(username);
		userService.setContext(user.getId(), map.get("context"));
		return new ApiResponse<>(HttpStatus.OK.value(), "Context updated successfully.", null);
	}

	@DeleteMapping("/user/{id}")
	public ApiResponse<Void> delete(@PathVariable final int id) {
		log.info("DELETE /user/" + id);
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
