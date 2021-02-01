package com.stephenschafer.ami.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stephenschafer.ami.controller.FileData;
import com.stephenschafer.ami.controller.FileInfo;
import com.stephenschafer.ami.controller.Request;
import com.stephenschafer.ami.converter.MimeTypeConverter;
import com.stephenschafer.ami.converter.MimeTypeConverterProvider;
import com.stephenschafer.ami.jpa.AttributeId;
import com.stephenschafer.ami.jpa.FileAttributeDao;
import com.stephenschafer.ami.jpa.FileAttributeEntity;
import com.stephenschafer.ami.service.ThingService;
import com.stephenschafer.ami.service.WordService;

import lombok.extern.slf4j.Slf4j;

@Transactional
@Service
@Slf4j
public class FileHandler extends BaseHandler {
	@Autowired
	private FileAttributeDao fileAttributeDao;
	@Autowired
	private WordService wordService;
	@Autowired
	private ThingService thingService;
	@Value("${ami.rich-text.dir:./rich-text}")
	private String richTextDir;
	@Value("${ami.files.dir:./files}")
	private String filesDir;

	@Override
	public void saveAttribute(final Request request) {
		log.info("FileHandler.saveAttribute");
		final Integer attrDefnId = request.getInteger("attrDefnId");
		final Integer thingId = request.getInteger("thingId");
		final Request value = request.getRequest("value");
		saveAttributeValue(thingId, attrDefnId, value);
	}

	@Override
	public void saveAttributeValue(final int thingId, final int attrDefnId, final Object value) {
		log.info("FileHandler.saveAttribute " + thingId + ", " + attrDefnId);
		final FileAttributeEntity entity = new FileAttributeEntity();
		entity.setAttrDefnId(attrDefnId);
		entity.setThingId(thingId);
		final FileInfo fileInfo;
		if (value instanceof FileData) {
			final FileData fileData = (FileData) value;
			try {
				fileInfo = thingService.saveFile(fileData.getBytes(), fileData.getFilename(),
					fileData.getMimeType(), thingId, attrDefnId);
			}
			catch (final IOException e) {
				throw new RuntimeException("Failed to save file", e);
			}
		}
		else if (value instanceof FileInfo) {
			fileInfo = (FileInfo) value;
		}
		else if (value instanceof Request) {
			final Request request = (Request) value;
			final byte[] bytes = request.getBytes("bytes");
			if (bytes != null) {
				final String filename = request.getString("filename");
				final String mimeType = request.getString("mimeType");
				try {
					fileInfo = thingService.saveFile(bytes, filename, mimeType, thingId,
						attrDefnId);
				}
				catch (final IOException e) {
					throw new RuntimeException("Failed to save file", e);
				}
			}
			else {
				fileInfo = new FileInfo(request);
			}
		}
		else {
			throw new ClassCastException("Expecting either FileData or FileInfo");
		}
		entity.setFilename(fileInfo.getFilename());
		entity.setMimeType(fileInfo.getMimeType());
		fileAttributeDao.save(entity);
		final String html = fileInfo.getRichText();
		if (html != null) {
			final String pathName = richTextDir + "/" + thingId + "/" + attrDefnId;
			final Path path = Paths.get(pathName);
			log.info("path = " + path);
			try {
				final Path parent = path.getParent();
				if (parent != null) {
					Files.createDirectories(parent);
				}
				Files.write(path, html.getBytes());
			}
			catch (final IOException e) {
				log.error("failed to save rich-text to " + path, e);
				throw new RuntimeException("failed to save rich-text", e);
			}
		}
		wordService.updateIndex(thingId, attrDefnId);
	}

	@Override
	public FileInfo getAttributeValue(final int thingId, final int attrDefnId) {
		final AttributeId attributeId = new AttributeId();
		attributeId.setAttrDefnId(attrDefnId);
		attributeId.setThingId(thingId);
		final Optional<FileAttributeEntity> optional = fileAttributeDao.findById(attributeId);
		String filename;
		String mimeType;
		if (optional.isPresent()) {
			final FileAttributeEntity entity = optional.get();
			filename = entity.getFilename();
			mimeType = entity.getMimeType();
		}
		else {
			filename = null;
			mimeType = null;
		}
		final String pathName = richTextDir + "/" + thingId + "/" + attrDefnId;
		final Path path = Paths.get(pathName);
		byte[] bytes;
		try {
			bytes = Files.readAllBytes(path);
		}
		catch (final NoSuchFileException e) {
			bytes = new byte[0];
		}
		catch (final IOException e) {
			log.error("failed to read rich-text from " + path, e);
			bytes = new byte[0];
		}
		return new FileInfo(filename, mimeType, new String(bytes));
	}

	@Autowired
	private MimeTypeConverterProvider converterProvider;

	@Override
	protected Set<String> getWords(final int thingId, final int attrDefnId) {
		final FileInfo fileValue = getAttributeValue(thingId, attrDefnId);
		String richText = fileValue.getRichText();
		if (richText == null) {
			final ThingService.MimeType mimeType = thingService.getMimeType(
				fileValue.getMimeType());
			final MimeTypeConverter converter = converterProvider.getConverter(mimeType.getName());
			final String pathName = filesDir + "/" + thingId + "/" + attrDefnId;
			final File file = new File(pathName);
			if (file.exists()) {
				if (converter != null) {
					FileInputStream fis = null;
					try {
						fis = new FileInputStream(file);
					}
					catch (final FileNotFoundException e) {
					}
					if (fis != null) {
						richText = converter.convert(fis);
					}
				}
				else if (mimeType != null) {
					if (mimeType.isPlainText()) {
						final Path path = Paths.get(pathName);
						String text = null;
						try {
							text = Files.readString(path);
						}
						catch (final IOException e) {
							log.error("Unable to read file", e);
						}
						return wordService.parseWords(text);
					}
				}
			}
		}
		if (richText == null) {
			return new HashSet<>();
		}
		final Document doc = Jsoup.parse(richText);
		final String text = doc.text();
		return wordService.parseWords(text);
	}

	@Override
	public void deleteAttributesByThing(final Integer thingId) {
		fileAttributeDao.deleteByThingId(thingId);
	}

	@Override
	public String getHandlerName() {
		return "file";
	}
}
