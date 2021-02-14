package com.stephenschafer.ami.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stephenschafer.ami.controller.FileInfo;
import com.stephenschafer.ami.controller.User;
import com.stephenschafer.ami.converter.MimeTypeConverter;
import com.stephenschafer.ami.converter.MimeTypeConverterProvider;
import com.stephenschafer.ami.handler.Handler;
import com.stephenschafer.ami.handler.HandlerProvider;
import com.stephenschafer.ami.handler.LinkHandler;
import com.stephenschafer.ami.jpa.AttrDefnEntity;
import com.stephenschafer.ami.jpa.LinkAttributeEntity;
import com.stephenschafer.ami.jpa.ThingDao;
import com.stephenschafer.ami.jpa.ThingEntity;
import com.stephenschafer.ami.jpa.TypeDao;
import com.stephenschafer.ami.jpa.TypeEntity;
import com.stephenschafer.ami.jpa.UserTypeContextThingDao;
import com.stephenschafer.ami.jpa.UserTypeContextThingEntity;
import com.stephenschafer.ami.jpa.UserTypeThingDao;
import com.stephenschafer.ami.jpa.UserTypeThingEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@Service(value = "thingService")
public class ThingServiceImpl implements ThingService {
	@Autowired
	private ThingDao thingDao;
	@Autowired
	private TypeDao typeDao;
	@Autowired
	private UserTypeThingDao userTypeThingDao;
	@Autowired
	private UserTypeContextThingDao userTypeContextThingDao;
	@Autowired
	private HandlerProvider handlerProvider;
	@Autowired
	private WordService wordService;
	@Autowired
	private TypeService typeService;
	@Autowired
	private AttrDefnService attrDefnService;
	@Autowired
	private LinkHandler linkHandler;
	@Autowired
	private UserService userService;
	@Autowired
	private MimeTypeConverterProvider converterProvider;
	@Value("${ami.files.dir:./files}")
	private String filesDir;

	@Override
	public ThingEntity insert(final ThingEntity thing) {
		thing.setId(null);
		return update(thing);
	}

	@Override
	public ThingEntity update(final ThingEntity thing) {
		final ThingEntity entity = thingDao.save(thing);
		wordService.updateIndex(entity.getId());
		return entity;
	}

	@Override
	public List<ThingEntity> findByTypeId(final int typeId) {
		return thingDao.findByTypeId(typeId);
	}

	@Override
	public Iterable<ThingEntity> findAll() {
		return thingDao.findAll();
	}

	@Override
	public void delete(final int thingId) {
		final List<Handler> handlers = handlerProvider.getAllHandlers();
		for (final Handler handler : handlers) {
			handler.deleteAttributesByThing(thingId);
		}
		wordService.deleteIndex(thingId);
		thingDao.deleteById(thingId);
	}

	@Override
	public ThingEntity findById(final int thingId) {
		final Optional<ThingEntity> optional = thingDao.findById(thingId);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public String getName(final ThingEntity thing) {
		final StringBuilder sb = new StringBuilder();
		final int typeId = thing.getTypeId();
		final AttrDefnEntity attrDefn = attrDefnService.findByName(typeId, "name");
		if (attrDefn != null) {
			final Handler handler = handlerProvider.getHandler(attrDefn.getHandler());
			final Object presObj = handler.getAttributeValue(thing.getId(), attrDefn.getId());
			if (presObj != null) {
				sb.append(presObj.toString());
			}
			else {
				sb.append("thing ");
				sb.append(thing.getId().toString());
			}
		}
		else {
			sb.append("thing ");
			sb.append(thing.getId().toString());
		}
		return sb.toString();
	}

	@Override
	public MimeType getMimeType(final ThingEntity thing) {
		try {
			final String mimeType = this.getAttributeStringValue(thing, "mime-type");
			if (mimeType != null) {
				return new MimeType(mimeType);
			}
		}
		catch (final AttributeNotFoundException e) {
		}
		try {
			final String mimeType = this.getAttributeStringValue(thing, "content-type");
			if (mimeType != null) {
				return new MimeType(mimeType);
			}
		}
		catch (final AttributeNotFoundException e1) {
		}
		return null;
	}

	@Override
	public String getText(final ThingEntity thing) {
		try {
			return this.getAttributeStringValue(thing, "text");
		}
		catch (final AttributeNotFoundException e) {
			return null;
		}
	}

	@Override
	public String getPresentation(final ThingEntity thing, final boolean includeType) {
		final StringBuilder sb = new StringBuilder();
		final int typeId = thing.getTypeId();
		if (includeType) {
			final Optional<TypeEntity> optional = typeDao.findById(typeId);
			if (optional.isPresent()) {
				final TypeEntity type = optional.get();
				sb.append(type.getName());
				sb.append(": ");
			}
		}
		final AttrDefnEntity attrDefn = attrDefnService.findByName(typeId, "name");
		if (attrDefn != null) {
			final Handler handler = handlerProvider.getHandler(attrDefn.getHandler());
			final Object presObj = handler.getAttributeValue(thing.getId(), attrDefn.getId());
			if (presObj != null) {
				sb.append(presObj.toString());
			}
			else {
				sb.append("thing ");
				sb.append(thing.getId().toString());
			}
		}
		else {
			sb.append("thing ");
			sb.append(thing.getId().toString());
		}
		return sb.toString();
	}

	@Override
	public Integer getParentId(final ThingEntity thing) {
		final int typeId = thing.getTypeId();
		final AttrDefnEntity attrDefn = attrDefnService.findByName(typeId, "parent");
		if (attrDefn != null) {
			final Handler handler = handlerProvider.getHandler(attrDefn.getHandler());
			final Object parentsObj = handler.getAttributeValue(thing.getId(), attrDefn.getId());
			if (parentsObj instanceof List) {
				@SuppressWarnings("unchecked")
				final List<Integer> parents = (List<Integer>) parentsObj;
				if (!parents.isEmpty()) {
					return parents.get(0);
				}
			}
		}
		return null;
	}

	private Map<String, Object> getSelectOption(final ThingEntity thing,
			final boolean includeTypeName) {
		final Map<String, Object> thingMap = new HashMap<>();
		thingMap.put("id", thing.getId());
		thingMap.put("presentation", getPresentation(thing, includeTypeName));
		return thingMap;
	}

	@Override
	public List<Map<String, Object>> getSelectOptions() {
		log.info("getSelectOptions");
		final long startTime = System.currentTimeMillis();
		final List<Map<String, Object>> thingList = new ArrayList<>();
		final Iterable<ThingEntity> things = this.findAll();
		for (final ThingEntity thing : things) {
			final Map<String, Object> thingMap = this.getSelectOption(thing, true);
			thingList.add(thingMap);
		}
		log.info("  elapsed: " + (System.currentTimeMillis() - startTime));
		return thingList;
	}

	@Override
	public List<Map<String, Object>> getSelectOptions(final int typeId) {
		log.info("getSelectOptions " + typeId);
		final long startTime = System.currentTimeMillis();
		final List<Map<String, Object>> thingList = new ArrayList<>();
		final List<ThingEntity> things = this.findByTypeId(typeId);
		log.info("  thing count: " + things.size());
		for (final ThingEntity thing : things) {
			final Map<String, Object> thingMap = this.getSelectOption(thing, false);
			thingList.add(thingMap);
		}
		log.info("  elapsed: " + (System.currentTimeMillis() - startTime));
		return thingList;
	}

	@Override
	public void updateThingOrder(final int userId, final int typeId, final List<Integer> thingIds) {
		userTypeThingDao.deleteByUserIdAndTypeId(userId, typeId);
		int sortOrder = 0;
		for (final Integer thingId : thingIds) {
			final UserTypeThingEntity entity = new UserTypeThingEntity(userId, typeId, thingId,
					sortOrder++);
			userTypeThingDao.save(entity);
		}
	}

	@Override
	public void updateThingOrder(final int userId, final int typeId, final int contextThingId,
			final List<Integer> thingIds) {
		userTypeContextThingDao.deleteByUserIdAndTypeIdAndContextThingId(userId, typeId,
			contextThingId);
		int sortOrder = 0;
		for (final Integer thingId : thingIds) {
			final UserTypeContextThingEntity entity = new UserTypeContextThingEntity(userId, typeId,
					contextThingId, thingId, sortOrder++);
			userTypeContextThingDao.save(entity);
		}
	}

	@Override
	public List<Integer> getThingOrder(final int userId, final int typeId) {
		log.info("getThingOrder userId = " + userId + ", typeId = " + typeId);
		final List<UserTypeThingEntity> list = userTypeThingDao.findByUserIdAndTypeId(userId,
			typeId);
		Collections.sort(list, new Comparator<UserTypeThingEntity>() {
			@Override
			public int compare(final UserTypeThingEntity o1, final UserTypeThingEntity o2) {
				final Integer so1 = o1.getSortOrder();
				final Integer so2 = o2.getSortOrder();
				return so1.compareTo(so2);
			}
		});
		log.info("getThingOrder " + list);
		final List<Integer> result = new ArrayList<>();
		for (final UserTypeThingEntity entity : list) {
			result.add(entity.getThingId());
		}
		return result;
	}

	@Override
	public List<Integer> getThingOrder(final int userId, final int typeId,
			final int contextThingId) {
		log.info("getThingOrder userId = " + userId + ", typeId = " + typeId + ", contextThingId = "
			+ contextThingId);
		final List<UserTypeContextThingEntity> list = userTypeContextThingDao.findByUserIdAndTypeIdAndContextThingId(
			userId, typeId, contextThingId);
		Collections.sort(list, new Comparator<UserTypeContextThingEntity>() {
			@Override
			public int compare(final UserTypeContextThingEntity o1,
					final UserTypeContextThingEntity o2) {
				final Integer so1 = o1.getSortOrder();
				final Integer so2 = o2.getSortOrder();
				return so1.compareTo(so2);
			}
		});
		log.info("getThingOrder " + list);
		final List<Integer> result = new ArrayList<>();
		for (final UserTypeContextThingEntity entity : list) {
			result.add(entity.getThingId());
		}
		return result;
	}

	@Override
	public Map<Integer, Set<Integer>> getSourceLinks(final int thingId) {
		log.info("getSourceLinks " + thingId);
		final List<LinkAttributeEntity> sourceLinks = linkHandler.findByTargetThingId(thingId);
		log.info("  sourceLinks: " + sourceLinks.size());
		final Map<Integer, Set<Integer>> links = new HashMap<>();
		for (final LinkAttributeEntity linkAttribute : sourceLinks) {
			final int attrDefnId = linkAttribute.getAttributeDefnId();
			Set<Integer> thingIds = links.get(attrDefnId);
			if (thingIds == null) {
				thingIds = new HashSet<>();
				links.put(attrDefnId, thingIds);
			}
			thingIds.add(linkAttribute.getThingId());
		}
		return links;
	}

	@Override
	public Set<Integer> getSourceLinks(final int thingId, final int attrDefnId) {
		log.info("getSourceLinks " + thingId + ", " + attrDefnId);
		final List<LinkAttributeEntity> sourceLinks = linkHandler.findByTargetThingIdAndAttributeDefnId(
			thingId, attrDefnId);
		log.info("  sourceLinks: " + sourceLinks.size());
		final Set<Integer> links = new HashSet<>();
		for (final LinkAttributeEntity linkAttribute : sourceLinks) {
			if (attrDefnId == linkAttribute.getAttributeDefnId()) {
				links.add(linkAttribute.getThingId());
			}
		}
		return links;
	}

	@Override
	public Map<String, Object> getAttributeValues(final ThingEntity thing) {
		final int typeId = thing.getTypeId();
		final List<AttrDefnEntity> attrDefns = attrDefnService.findByTypeIdOrderBySortOrder(typeId);
		final Map<String, Object> attrMap = new HashMap<>();
		for (final AttrDefnEntity attrDefn : attrDefns) {
			final Handler handler = handlerProvider.getHandler(attrDefn.getHandler());
			final Map<String, Object> attrDefnMap = handler.getAttrDefnMap(attrDefn);
			attrDefnMap.put("value", handler.getAttributeValue(thing.getId(), attrDefn.getId()));
			attrMap.put(attrDefn.getName(), attrDefnMap);
		}
		return attrMap;
	}

	@Getter
	@Setter
	@ToString
	public class FindThingResult {
		private int id;
		private User creator;
		private Date created;
		private TypeEntity type;
		private Map<String, Object> attributes;
		private Map<Integer, Set<Integer>> links;
	}

	public FindThingResult getFindThingResult(final ThingEntity thing) {
		log.info("getFindThingResult " + thing);
		if (thing == null) {
			return null;
		}
		final FindThingResult result = new FindThingResult();
		result.setId(thing.getId());
		result.setCreated(thing.getCreated());
		result.setCreator(new User(userService.findById(thing.getCreator())));
		final int typeId = thing.getTypeId();
		final TypeEntity type = typeService.findById(typeId);
		result.setType(type);
		final List<AttrDefnEntity> attrDefns = attrDefnService.findByTypeIdOrderBySortOrder(typeId);
		log.info("  attrDefns: " + attrDefns.size());
		final Map<String, Object> attrMap = new HashMap<>();
		for (final AttrDefnEntity attrDefn : attrDefns) {
			final Handler handler = handlerProvider.getHandler(attrDefn.getHandler());
			final Map<String, Object> attrDefnMap = handler.getAttrDefnMap(attrDefn);
			attrDefnMap.put("value", handler.getAttributeValue(thing.getId(), attrDefn.getId()));
			attrMap.put(attrDefn.getName(), attrDefnMap);
		}
		result.setAttributes(attrMap);
		final List<LinkAttributeEntity> sourceLinks = linkHandler.findByTargetThingId(
			thing.getId());
		log.info("  sourceLinks: " + sourceLinks.size());
		final Map<Integer, Set<Integer>> links = new HashMap<>();
		for (final LinkAttributeEntity linkAttribute : sourceLinks) {
			final int attrDefnId = linkAttribute.getAttributeDefnId();
			Set<Integer> thingIds = links.get(attrDefnId);
			if (thingIds == null) {
				thingIds = new HashSet<>();
				links.put(attrDefnId, thingIds);
			}
			thingIds.add(linkAttribute.getThingId());
		}
		result.setLinks(links);
		return result;
	}

	@Override
	public String getAttributeStringValue(final ThingEntity thing, final String attrName)
			throws AttributeNotFoundException {
		final AttrDefnEntity attrDefn = attrDefnService.findByName(thing.getTypeId(), attrName);
		if (attrDefn == null) {
			throw new AttributeNotFoundException("Attribute named '" + attrName + "' not found");
		}
		final String handlerName = attrDefn.getHandler();
		final Handler handler = handlerProvider.getHandler(handlerName);
		final Object valueObj = handler.getAttributeValue(thing.getId(), attrDefn.getId());
		return valueObj == null ? null : valueObj.toString();
	}

	public void saveAttributeValue(final ThingEntity thing, final String attrName,
			final Object attrValue) throws AttributeNotFoundException {
		final AttrDefnEntity attrDefn = attrDefnService.findByName(thing.getTypeId(), attrName);
		if (attrDefn == null) {
			throw new AttributeNotFoundException("Attribute named '" + attrName + "' not found");
		}
		final String handlerName = attrDefn.getHandler();
		final Handler handler = handlerProvider.getHandler(handlerName);
		handler.saveAttributeValue(thing.getId(), attrDefn.getId(), attrValue);
	}

	public void saveAttributeValue(final ThingEntity thing, final int attrDefnId,
			final Object attrValue) throws AttributeNotFoundException {
		final AttrDefnEntity attrDefn = attrDefnService.findById(attrDefnId);
		if (attrDefn == null) {
			throw new AttributeNotFoundException("Attribute '" + attrDefnId + "' not found");
		}
		final String handlerName = attrDefn.getHandler();
		final Handler handler = handlerProvider.getHandler(handlerName);
		handler.saveAttributeValue(thing.getId(), attrDefn.getId(), attrValue);
	}

	@Override
	public FileInfo saveFile(final byte[] bytes, final String filename, final String contentType,
			final int thingId, final int attrId) throws IOException {
		log.info("ThingService.saveFile");
		log.info("bytes.length = " + bytes.length);
		log.info("file filename = " + filename);
		final String pathName = filesDir + "/" + thingId + "/" + attrId;
		final Path path = Paths.get(pathName);
		log.info("path = " + path);
		log.info("contentType = " + contentType);
		Files.createDirectories(path.getParent());
		Files.write(path, bytes);
		final MimeTypeConverter converter = converterProvider.getConverter(contentType);
		final String html;
		if (converter != null) {
			html = converter.convert(new ByteArrayInputStream(bytes));
		}
		else {
			html = null;
			log.info(MessageFormat.format("converter for {0} not found", contentType));
		}
		return new FileInfo(filename, contentType, html);
	}

	@Override
	public ThingEntity save(final ThingEntity thing) {
		return thingDao.save(thing);
	}

	@Override
	public MimeType getMimeType(final String string) {
		if (string == null) {
			return null;
		}
		return new MimeType(string);
	}
}
