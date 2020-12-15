package com.stephenschafer.ami.controller;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {
	@ExceptionHandler(RuntimeException.class)
	public ApiResponse<Void> handleRuntimeException(final RuntimeException ex) {
		log.error("Exception", ex);
		final ApiResponse<Void> apiResponse = new ApiResponse<>(400, "Bad request", null);
		return apiResponse;
	}

	@ExceptionHandler(MultipartException.class)
	public ApiResponse<Void> handleMultipartException(final MultipartException e,
			final RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("message", e.getCause().getMessage());
		log.error("Exception", e);
		final ApiResponse<Void> apiResponse = new ApiResponse<>(400, "Multipart Exception", null);
		return apiResponse;
	}
}
