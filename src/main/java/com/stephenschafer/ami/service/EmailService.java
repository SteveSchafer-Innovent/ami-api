package com.stephenschafer.ami.service;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;

public interface EmailService {
	Set<EmailProperties> getEmailProperties(String emailServerName) throws EmailConfigException;

	int downloadEmails(EmailProperties emailProperties) throws EmailConfigException,
			AttributeNotFoundException, IOException, NoSuchProviderException, MessagingException;

	CompletableFuture<Integer> submitDownloadEmails(EmailProperties emailProperties);
}
