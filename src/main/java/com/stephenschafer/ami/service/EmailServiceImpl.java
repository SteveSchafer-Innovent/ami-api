package com.stephenschafer.ami.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stephenschafer.ami.controller.FileData;
import com.stephenschafer.ami.handler.DateTimeHandler;
import com.stephenschafer.ami.handler.FileHandler;
import com.stephenschafer.ami.handler.Handler;
import com.stephenschafer.ami.handler.HandlerProvider;
import com.stephenschafer.ami.handler.LinkHandler;
import com.stephenschafer.ami.handler.RichTextHandler;
import com.stephenschafer.ami.handler.StringHandler;
import com.stephenschafer.ami.jpa.AttrDefnEntity;
import com.stephenschafer.ami.jpa.LinkAttributeEntity;
import com.stephenschafer.ami.jpa.ThingEntity;
import com.stephenschafer.ami.jpa.TypeEntity;
import com.stephenschafer.ami.service.ThingService.MimeType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service(value = "emailService")
public class EmailServiceImpl implements EmailService {
	@Autowired
	private TypeService typeService;
	@Autowired
	private AttrDefnService attrDefnService;
	@Autowired
	private ThingService thingService;
	@Autowired
	private HandlerProvider handlerProvider;
	@Autowired
	private LinkHandler linkHandler;
	@Autowired
	private StringHandler stringHandler;
	@Autowired
	private RichTextHandler htmlHandler;
	@Autowired
	private FileHandler fileHandler;
	@Autowired
	private DateTimeHandler dateTimeHandler;
	private final Executor executor;

	public EmailServiceImpl() {
		final ForkJoinPool.ForkJoinWorkerThreadFactory threadFactory = new ThreadFactory();
		executor = new ForkJoinPool(4, threadFactory, null, false);
	}

	@Transactional
	@Override
	public Set<EmailProperties> getEmailProperties(final String emailServerName)
			throws EmailConfigException {
		final TypeEntity type = typeService.findByName("email-server");
		if (type == null) {
			throw new EmailConfigException("Type named email-server not found");
		}
		final AttrDefnEntity nameAttrDefn = attrDefnService.findByName(type.getId(), "name");
		if (nameAttrDefn == null) {
			throw new EmailConfigException(
					"Email server named '" + emailServerName + "' not found");
		}
		final String handlerName = nameAttrDefn.getHandler();
		final List<ThingEntity> emailServers = thingService.findByTypeId(type.getId());
		final Handler handler = handlerProvider.getHandler(handlerName);
		final Set<ThingEntity> designatedEmailServers = new HashSet<>();
		for (final ThingEntity emailServer : emailServers) {
			final Object attrValue = handler.getAttributeValue(emailServer.getId(),
				nameAttrDefn.getId());
			if (attrValue != null && attrValue.toString().equalsIgnoreCase(emailServerName)) {
				designatedEmailServers.add(emailServer);
			}
		}
		final Set<EmailProperties> resultSet = new HashSet<>();
		for (final ThingEntity thing : designatedEmailServers) {
			final EmailProperties emailProperties = new EmailProperties();
			emailProperties.setUserId(thing.getCreator());
			try {
				emailProperties.setProtocol(
					thingService.getAttributeStringValue(thing, "protocol"));
				emailProperties.setHost(thingService.getAttributeStringValue(thing, "host"));
				emailProperties.setPort(thingService.getAttributeStringValue(thing, "port"));
				emailProperties.setUsername(
					thingService.getAttributeStringValue(thing, "username"));
				emailProperties.setPassword(
					thingService.getAttributeStringValue(thing, "password"));
				emailProperties.setThingId(thing.getId());
			}
			catch (final AttributeNotFoundException e) {
				throw new EmailConfigException("Attribute not found", e);
			}
			resultSet.add(emailProperties);
		}
		return resultSet;
	}

	/**
	 * Returns a Properties object which is configured for a POP3/IMAP server
	 *
	 * @param protocol
	 *            either "imap" or "pop3"
	 * @param host
	 * @param port
	 * @return a Properties object
	 */
	private Properties getServerProperties(final String protocol, final String host,
			final String port) {
		final Properties properties = new Properties();
		// server setting
		// properties.put(String.format("mail.%s.host", protocol), host);
		// properties.put(String.format("mail.%s.port", protocol), port);
		// SSL setting
		//		properties.setProperty(String.format("mail.%s.socketFactory.class", protocol),
		//			"javax.net.ssl.SSLSocketFactory");
		//		properties.setProperty(String.format("mail.%s.socketFactory.fallback", protocol), "false");
		//		properties.setProperty(String.format("mail.%s.socketFactory.port", protocol),
		//			String.valueOf(port));
		properties.setProperty(String.format("mail.%s.ssl.enable", protocol), "true");
		// properties.setProperty(String.format("mail.%s.starttls.enable", protocol), "true");
		return properties;
	}

	@Getter
	@Setter
	@ToString
	private static class EmailDownloadContext {
		private final EmailServiceImpl emailService;
		private final int userId;
		private final int messageTypeId;
		private final int partTypeId;
		private final int addressTypeId;
		private final int folderTypeId;
		private final int nameAttrDefnId;
		private final int parentAttrDefnId;
		private final int messageIdAttrDefnId;
		private final int folderAttrDefnId;
		private final int fromAttrDefnId;
		private final int toAttrDefnId;
		private final int ccAttrDefnId;
		private final int bccAttrDefnId;
		private final int subjectAttrDefnId;
		private final int dateAttrDefnId;
		private final int contentTypeAttrDefnId;
		private final int partParentAttrDefnId;
		private final int fileAttrDefnId;
		private final int textAttrDefnId;
		private final int htmlAttrDefnId;
		private final int addressAttrDefnId;
		private final int personalAttrDefnId;
		private final Map<String, AddressCacheEntry> addressCache;
		private final Map<String, Integer> messageCache;

		public EmailDownloadContext(final int userId, final EmailServiceImpl emailService) {
			this.userId = userId;
			this.emailService = emailService;
			messageTypeId = emailService.typeService.getOrCreate("email-message", userId).getId();
			partTypeId = emailService.typeService.getOrCreate("email-part", userId).getId();
			addressTypeId = emailService.typeService.getOrCreate("email-address", userId).getId();
			folderTypeId = emailService.typeService.getOrCreate("email-folder", userId).getId();
			nameAttrDefnId = emailService.stringHandler.getOrCreateAttrDefn(folderTypeId, "name",
				false, true, false).getId();
			parentAttrDefnId = emailService.linkHandler.getOrCreateAttrDefn(folderTypeId, "parent",
				false, true, false).getId();
			messageIdAttrDefnId = emailService.stringHandler.getOrCreateAttrDefn(messageTypeId,
				"Message-ID", false, false, false).getId();
			folderAttrDefnId = emailService.linkHandler.getOrCreateAttrDefn(messageTypeId, "folder",
				false, true, false).getId();
			fromAttrDefnId = emailService.linkHandler.getOrCreateAttrDefn(messageTypeId, "from",
				false, true, false).getId();
			toAttrDefnId = emailService.linkHandler.getOrCreateAttrDefn(messageTypeId, "to", false,
				true, false).getId();
			ccAttrDefnId = emailService.linkHandler.getOrCreateAttrDefn(messageTypeId, "cc", false,
				false, false).getId();
			bccAttrDefnId = emailService.linkHandler.getOrCreateAttrDefn(messageTypeId, "bcc",
				false, false, false).getId();
			subjectAttrDefnId = emailService.stringHandler.getOrCreateAttrDefn(messageTypeId,
				"subject", false, true, false).getId();
			dateAttrDefnId = emailService.stringHandler.getOrCreateAttrDefn(messageTypeId,
				"sent-date", false, true, false).getId();
			contentTypeAttrDefnId = emailService.stringHandler.getOrCreateAttrDefn(partTypeId,
				"content-type", false, false, false).getId();
			partParentAttrDefnId = emailService.linkHandler.getOrCreateAttrDefn(partTypeId,
				"parent", false, false, false).getId();
			fileAttrDefnId = emailService.fileHandler.getOrCreateAttrDefn(partTypeId, "file", false,
				false, false).getId();
			textAttrDefnId = emailService.stringHandler.getOrCreateAttrDefn(partTypeId, "text",
				false, false, false).getId();
			htmlAttrDefnId = emailService.htmlHandler.getOrCreateAttrDefn(partTypeId, "html", false,
				false, false).getId();
			addressAttrDefnId = emailService.stringHandler.getOrCreateAttrDefn(addressTypeId,
				"address", false, true, false).getId();
			personalAttrDefnId = emailService.stringHandler.getOrCreateAttrDefn(addressTypeId,
				"personal", true, false, false).getId();
			final Map<String, AddressCacheEntry> addressCache = new HashMap<>();
			final List<ThingEntity> addressThings = emailService.thingService.findByTypeId(
				addressTypeId);
			for (final ThingEntity addressThing : addressThings) {
				final int thingId = addressThing.getId();
				final String address = emailService.stringHandler.getAttributeValue(thingId,
					addressAttrDefnId);
				final String personal = emailService.stringHandler.getAttributeValue(thingId,
					personalAttrDefnId);
				addressCache.put(address, new AddressCacheEntry(thingId, personal));
			}
			log.info("addresses loaded into cache: " + addressCache.size());
			this.addressCache = addressCache;
			final Map<String, Integer> messageCache = new HashMap<>();
			final List<ThingEntity> messageThings = emailService.thingService.findByTypeId(
				messageTypeId);
			for (final ThingEntity messageThing : messageThings) {
				final int thingId = messageThing.getId();
				final String messageId = emailService.stringHandler.getAttributeValue(thingId,
					messageIdAttrDefnId);
				if (messageId != null) {
					messageCache.put(messageId, Integer.valueOf(thingId));
				}
			}
			log.info("messages loaded into cache: " + messageCache.size());
			this.messageCache = messageCache;
		}

		public int addAddress(final String emailAddress, final String personal) {
			final AddressCacheEntry entry = addressCache.get(emailAddress);
			if (entry != null) {
				return entry.getThingId();
			}
			ThingEntity addressThing = new ThingEntity();
			addressThing.setTypeId(addressTypeId);
			addressThing.setCreated(new Date());
			addressThing.setCreator(userId);
			addressThing = emailService.thingService.save(addressThing);
			final int thingId = addressThing.getId();
			emailService.stringHandler.saveAttributeValue(thingId, addressAttrDefnId, emailAddress);
			if (personal != null) {
				emailService.stringHandler.saveAttributeValue(thingId, personalAttrDefnId,
					personal);
			}
			addressCache.put(emailAddress, new AddressCacheEntry(thingId, personal));
			return thingId;
		}
	}

	@Getter
	@Setter
	@ToString
	@AllArgsConstructor
	private static class AddressCacheEntry {
		private final int thingId;
		private final String personal;
	}

	private static class ThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {
		@Override
		public ForkJoinWorkerThread newThread(final ForkJoinPool pool) {
			return new ForkJoinWorkerThread(pool) {
			};
		}
	}

	@Override
	public CompletableFuture<Integer> submitDownloadEmails(final EmailProperties emailProperties) {
		final CompletableFuture<Integer> future = CompletableFuture.supplyAsync((() -> {
			try {
				final int messageCount = downloadEmails(emailProperties);
				return Integer.valueOf(messageCount);
			}
			catch (final Exception e) {
				throw new RuntimeException("Failed to download emails", e);
			}
		}), executor);
		return future;
	}

	@Override
	public int downloadEmails(final EmailProperties emailProperties) throws EmailConfigException,
			AttributeNotFoundException, IOException, MessagingException {
		log.info("downloadEmails, emailProperties = " + emailProperties);
		final Properties properties = getServerProperties(emailProperties.getProtocol(),
			emailProperties.getHost(), emailProperties.getPort());
		log.info("properties = " + properties);
		final EmailDownloadContext context = new EmailDownloadContext(emailProperties.getUserId(),
				this);
		final Session session = Session.getDefaultInstance(properties, null);
		final Store store = session.getStore(emailProperties.getProtocol());
		store.connect(emailProperties.getHost(), emailProperties.getUsername(),
			emailProperties.getPassword());
		try {
			final Folder rootFolder = store.getDefaultFolder();
			return downloadEmails(context, rootFolder, emailProperties.getThingId());
		}
		finally {
			store.close();
		}
	}

	@Transactional
	private int downloadEmails(final EmailDownloadContext context, final Folder folder,
			final int folderThingId) throws MessagingException, EmailConfigException,
			AttributeNotFoundException, IOException {
		int messageCount = 0;
		if (!"Trash".equalsIgnoreCase(folder.getName())
			&& !"Junk".equalsIgnoreCase(folder.getName())) {
			if ((folder.getType() & Folder.HOLDS_MESSAGES) != 0) {
				folder.open(Folder.READ_ONLY);
				try {
					final Message[] messages = folder.getMessages();
					log.info(
						"reading folder " + folder.getName() + ", messages: " + messages.length);
					int i = 0;
					for (final Message message : messages) {
						log.info("reading message " + (++i));
						saveMessage(context, message, folderThingId);
						messageCount += 1;
					}
				}
				finally {
					folder.close(false);
				}
			}
			final Folder[] children = folder.list();
			for (final Folder child : children) {
				final String childName = child.getName();
				ThingEntity childFolderThing = new ThingEntity();
				final int typeId = context.getFolderTypeId();
				final int nameAttrDefnId = context.getNameAttrDefnId();
				final int parentAttrDefnId = context.getParentAttrDefnId();
				Integer childFolderThingId = null;
				// see if this folder is already there
				for (final LinkAttributeEntity entity : linkHandler.findByTargetThingId(
					folderThingId)) {
					final String name = stringHandler.getAttributeValue(entity.getThingId(),
						nameAttrDefnId);
					if (childName.equals(name)) {
						childFolderThingId = entity.getThingId();
						break;
					}
				}
				if (childFolderThingId == null) {
					// create a new one
					childFolderThing.setTypeId(typeId);
					childFolderThing.setCreated(new Date());
					childFolderThing.setCreator(context.getUserId());
					childFolderThing = thingService.save(childFolderThing);
					childFolderThingId = childFolderThing.getId();
					stringHandler.saveAttributeValue(childFolderThingId, nameAttrDefnId, childName);
					linkHandler.saveAttributeValue(childFolderThingId, parentAttrDefnId,
						folderThingId);
				}
				messageCount += downloadEmails(context, child, childFolderThingId);
			}
		}
		return messageCount;
	}

	private void saveMessage(final EmailDownloadContext context, final Message message,
			final int folderThingId) throws MessagingException, EmailConfigException,
			AttributeNotFoundException, IOException {
		final String[] messageIds = message.getHeader("Message-ID");
		final String messageId = messageIds != null && messageIds.length >= 1 ? messageIds[0]
			: null;
		log.info("Message-ID = " + messageId);
		final int typeId = context.getMessageTypeId();
		final int messageIdAttrDefnId = context.getMessageIdAttrDefnId();
		final Map<String, Integer> messageCache = context.getMessageCache();
		Integer messageThingId = null;
		if (messageId != null) {
			if (messageCache.containsKey(messageId)) {
				messageThingId = messageCache.get(messageId);
			}
		}
		if (messageThingId == null) {
			// create a new one
			ThingEntity messageThing = new ThingEntity();
			messageThing.setTypeId(typeId);
			messageThing.setCreated(new Date());
			messageThing.setCreator(context.getUserId());
			messageThing = thingService.save(messageThing);
			messageThingId = messageThing.getId();
			if (messageId != null) {
				messageCache.put(messageId, messageThingId);
				stringHandler.saveAttributeValue(messageThingId, messageIdAttrDefnId, messageId);
			}
			linkHandler.saveAttributeValue(messageThingId, context.getFolderAttrDefnId(),
				folderThingId);
			linkHandler.saveAttributeValue(messageThingId, context.getFromAttrDefnId(),
				saveAddresses(context, message.getFrom()));
			linkHandler.saveAttributeValue(messageThingId, context.getToAttrDefnId(),
				saveAddresses(context, message.getRecipients(RecipientType.TO)));
			linkHandler.saveAttributeValue(messageThingId, context.getCcAttrDefnId(),
				saveAddresses(context, message.getRecipients(RecipientType.CC)));
			linkHandler.saveAttributeValue(messageThingId, context.getBccAttrDefnId(),
				saveAddresses(context, message.getRecipients(RecipientType.BCC)));
			stringHandler.saveAttributeValue(messageThingId, context.getSubjectAttrDefnId(),
				message.getSubject());
			final Date sentDate = message.getSentDate();
			if (sentDate != null) {
				dateTimeHandler.saveAttributeValue(messageThingId, context.getDateAttrDefnId(),
					sentDate);
			}
			savePart(context, messageThingId, message);
		}
	}

	private void savePart(final EmailDownloadContext context, final int thingId, final Part part)
			throws AttributeNotFoundException, IOException, MessagingException {
		final String contentType = part.getContentType();
		final MimeType mimeType = new MimeType(contentType);
		stringHandler.saveAttributeValue(thingId, context.getContentTypeAttrDefnId(), contentType);
		final Object content = part.getContent();
		if (content instanceof Multipart) {
			final Multipart multipartContent = (Multipart) content;
			final int count = multipartContent.getCount();
			for (int i = 0; i < count; i++) {
				final Part bodyPart = multipartContent.getBodyPart(i);
				ThingEntity partThing = new ThingEntity();
				partThing.setTypeId(context.getPartTypeId());
				partThing.setCreated(new Date());
				partThing.setCreator(context.getUserId());
				partThing = thingService.save(partThing);
				linkHandler.saveAttributeValue(partThing.getId(), context.getPartParentAttrDefnId(),
					thingId);
				savePart(context, partThing.getId(), bodyPart);
			}
		}
		else if (content instanceof String) {
			final String string = (String) content;
			if (string.length() > 8192) {
				final FileData fileData = new FileData(part.getFileName(), contentType,
						string.getBytes());
				fileHandler.saveAttributeValue(thingId, context.getFileAttrDefnId(), fileData);
			}
			else {
				if ("text/html".equalsIgnoreCase(mimeType.getName())) {
					htmlHandler.saveAttributeValue(thingId, context.getHtmlAttrDefnId(), string);
				}
				else {
					stringHandler.saveAttributeValue(thingId, context.getTextAttrDefnId(), string);
				}
			}
		}
		else if (content instanceof InputStream) {
			final InputStream inputStream = (InputStream) content;
			byte[] bytes;
			try {
				final ByteArrayOutputStream baos = new ByteArrayOutputStream();
				final byte[] buffer = new byte[0x1000];
				int bytesRead = inputStream.read(buffer);
				while (bytesRead >= 0) {
					baos.write(buffer, 0, bytesRead);
					bytesRead = inputStream.read(buffer);
				}
				baos.flush();
				bytes = baos.toByteArray();
			}
			catch (final Exception e) {
				log.error("Decoding problem", e);
				bytes = new byte[0];
			}
			finally {
				inputStream.close();
			}
			final FileData fileData = new FileData(part.getFileName(), contentType, bytes);
			fileHandler.saveAttributeValue(thingId, context.getFileAttrDefnId(), fileData);
		}
		else if (content instanceof byte[]) {
			final byte[] bytes = (byte[]) content;
			final FileData fileData = new FileData(part.getFileName(), contentType, bytes);
			fileHandler.saveAttributeValue(thingId, context.getFileAttrDefnId(), fileData);
		}
		else if (content == null) {
			log.info("Content is null");
		}
		else {
			log.info("Content class unrecognized: " + content.getClass().getName());
		}
	}

	private List<Integer> saveAddresses(final EmailDownloadContext context,
			final Address[] addresses) throws AttributeNotFoundException {
		final List<Integer> thingIds = new ArrayList<>();
		if (addresses != null) {
			for (final Address address : addresses) {
				String emailAddress;
				String personal;
				if (address instanceof InternetAddress) {
					final InternetAddress internetAddress = (InternetAddress) address;
					emailAddress = internetAddress.getAddress();
					personal = internetAddress.getPersonal();
				}
				else {
					emailAddress = address.toString();
					personal = null;
				}
				thingIds.add(context.addAddress(emailAddress, personal));
			}
		}
		return thingIds;
	}
}
