package com.stephenschafer.ami.handler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stephenschafer.ami.jpa.AttrDefnEntity;
import com.stephenschafer.ami.jpa.AttributeId;
import com.stephenschafer.ami.jpa.FileAttributeDao;
import com.stephenschafer.ami.jpa.FileAttributeEntity;
import com.stephenschafer.ami.jpa.ThingEntity;
import com.stephenschafer.ami.service.WordService;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Service
@Slf4j
public class FileHandler extends BaseHandler {
	@Autowired
	private FileAttributeDao fileAttributeDao;
	@Autowired
	private WordService wordService;
	@Value("${ami.rich-text.dir:./rich-text}")
	private String richTextDir;

	@Override
	public void saveAttribute(final Map<String, Object> map) {
		log.info("FileHandler.saveAttribute " + map);
		final FileAttributeEntity entity = new FileAttributeEntity();
		final Integer attrDefnId = (Integer) map.get("attrDefnId");
		entity.setAttrDefnId(attrDefnId);
		final Integer thingId = (Integer) map.get("thingId");
		entity.setThingId(thingId);
		@SuppressWarnings("unchecked")
		final Map<String, Object> value = (Map<String, Object>) map.get("value");
		entity.setFilename((String) value.get("filename"));
		entity.setMimeType((String) value.get("mimeType"));
		fileAttributeDao.save(entity);
		final String richText = (String) value.get("richText");
		final String pathName = richTextDir + "/" + thingId + "/" + attrDefnId;
		final Path path = Paths.get(pathName);
		log.info("path = " + path);
		try {
			Files.createDirectories(path.getParent());
			Files.write(path, richText.getBytes());
		}
		catch (final IOException e) {
			log.error("failed to save rich-text to " + path, e);
			throw new RuntimeException("failed to save rich-text", e);
		}
	}

	@Getter
	@Setter
	@ToString
	@AllArgsConstructor
	@NoArgsConstructor
	public static class FileValue {
		private String filename;
		private String mimeType;
		private String richText;
	}

	@Override
	public FileValue getAttributeValue(final ThingEntity thing, final AttrDefnEntity attrDefn) {
		return getAttributeValue(thing.getId(), attrDefn.getId());
	}

	public FileValue getAttributeValue(final int thingId, final int attrDefnId) {
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
		log.info("path = " + path);
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
		return new FileValue(filename, mimeType, new String(bytes));
	}

	@Override
	protected Set<String> getWords(final ThingEntity thing, final AttrDefnEntity attrDefn) {
		final FileValue fileValue = getAttributeValue(thing, attrDefn);
		final Document doc = Jsoup.parse(fileValue.richText);
		final String text = doc.text();
		return wordService.parseWords(text);
	}

	@Override
	public void deleteAttributesByThing(final Integer thingId) {
		fileAttributeDao.deleteByThingId(thingId);
	}
}
