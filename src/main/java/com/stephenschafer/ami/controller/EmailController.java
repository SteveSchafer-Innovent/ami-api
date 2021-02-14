package com.stephenschafer.ami.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stephenschafer.ami.service.AttributeNotFoundException;
import com.stephenschafer.ami.service.EmailConfigException;
import com.stephenschafer.ami.service.EmailProperties;
import com.stephenschafer.ami.service.EmailService;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class EmailController {
	@Autowired
	EmailService emailService;
	private final Map<String, CompletableFuture<Integer>> jobs = new HashMap<>();

	@GetMapping("/email/load")
	public ApiResponse<Integer> load(@RequestParam final String serverName)
			throws EmailConfigException, AttributeNotFoundException, IOException,
			NoSuchProviderException, MessagingException {
		log.info("GET /email/load serverName = " + serverName);
		final Set<EmailProperties> setOfEmailProperties = emailService.getEmailProperties(
			serverName);
		int messageCount = 0;
		for (final EmailProperties emailProperties : setOfEmailProperties) {
			messageCount += emailService.downloadEmails(emailProperties);
		}
		return new ApiResponse<>(HttpStatus.OK.value(), "Emails fetched successfully.",
				Integer.valueOf(messageCount));
	}

	@GetMapping("/email/start")
	public ApiResponse<Set<String>> start(@RequestParam final String serverName)
			throws EmailConfigException, AttributeNotFoundException, IOException,
			NoSuchProviderException, MessagingException {
		log.info("GET /email/start serverName = " + serverName);
		final Set<EmailProperties> setOfEmailProperties = emailService.getEmailProperties(
			serverName);
		final Set<String> jobIds = new HashSet<>();
		for (final EmailProperties emailProperties : setOfEmailProperties) {
			final UUID uuid = UUID.randomUUID();
			final String uuidString = uuid.toString();
			final CompletableFuture<Integer> future = emailService.submitDownloadEmails(
				emailProperties);
			jobs.put(uuidString, future);
			jobIds.add(uuidString);
		}
		return new ApiResponse<>(HttpStatus.OK.value(), "Emails download started successfully.",
				jobIds);
	}

	@Getter
	@Setter
	@ToString
	@AllArgsConstructor
	public static class Status {
		private final boolean exists;
		private final boolean done;
		private final boolean cancelled;
		private final boolean completedExceptionally;
		private final int messageCount;
	}

	@GetMapping("/email/status/{jobId}")
	public ApiResponse<Status> status(@PathVariable final String jobId) {
		log.info("GET /email/status/" + jobId);
		return new ApiResponse<>(HttpStatus.OK.value(),
				"Email download status fetched successfully.", getStatus(jobId));
	}

	@GetMapping("/email/all-status")
	public ApiResponse<Map<String, Status>> allStatus() {
		log.info("GET /email/all-status");
		final Map<String, Status> map = new HashMap<>();
		for (final String jobId : jobs.keySet()) {
			map.put(jobId, getStatus(jobId));
		}
		return new ApiResponse<>(HttpStatus.OK.value(),
				"Email download all-status fetched successfully.", map);
	}

	private Status getStatus(final String jobId) {
		final CompletableFuture<Integer> future = jobs.get(jobId);
		boolean exists = false;
		boolean done = false;
		boolean cancelled = false;
		boolean completedExceptionally = false;
		int messageCount = 0;
		if (future != null) {
			exists = true;
			done = future.isDone();
			cancelled = future.isCancelled();
			completedExceptionally = future.isCompletedExceptionally();
			if (done) {
				messageCount = future.getNow(null);
			}
		}
		return new Status(exists, done, cancelled, completedExceptionally, messageCount);
	}
}
