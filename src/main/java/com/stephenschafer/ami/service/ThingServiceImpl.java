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
import com.stephenschafer.ami.controller.FindThingResult;
import com.stephenschafer.ami.controller.User;
import com.stephenschafer.ami.converter.MimeTypeConverter;
import com.stephenschafer.ami.converter.MimeTypeConverterProvider;
import com.stephenschafer.ami.handler.Handler;
import com.stephenschafer.ami.handler.HandlerProvider;
import com.stephenschafer.ami.handler.LinkHandler;
import com.stephenschafer.ami.jpa.AttrDefnDao;
import com.stephenschafer.ami.jpa.AttrDefnEntity;
import com.stephenschafer.ami.jpa.BooleanAttributeDao;
import com.stephenschafer.ami.jpa.DateTimeAttributeDao;
import com.stephenschafer.ami.jpa.FileAttributeDao;
import com.stephenschafer.ami.jpa.FindTypeResult;
import com.stephenschafer.ami.jpa.FloatAttributeDao;
import com.stephenschafer.ami.jpa.IntegerAttributeDao;
import com.stephenschafer.ami.jpa.LinkAttributeEntity;
import com.stephenschafer.ami.jpa.StringAttributeDao;
import com.stephenschafer.ami.jpa.ThingDao;
import com.stephenschafer.ami.jpa.ThingEntity;
import com.stephenschafer.ami.jpa.TypeDao;
import com.stephenschafer.ami.jpa.TypeEntity;
import com.stephenschafer.ami.jpa.UserTypeContextThingDao;
import com.stephenschafer.ami.jpa.UserTypeContextThingEntity;
import com.stephenschafer.ami.jpa.UserTypeThingDao;
import com.stephenschafer.ami.jpa.UserTypeThingEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@Service(value = "thingService")
public class ThingServiceImpl implements ThingService {
	@Autowired
	private ThingDao thingDao;
	@Autowired
	private StringAttributeDao stringAttributeDao;
	@Autowired
	private IntegerAttributeDao integerAttributeDao;
	@Autowired
	private BooleanAttributeDao booleanAttributeDao;
	@Autowired
	private DateTimeAttributeDao dateTimeAttributeDao;
	@Autowired
	private FileAttributeDao fileAttributeDao;
	@Autowired
	private FloatAttributeDao floatAttributeDao;
	@Autowired
	private AttrDefnDao attrDefnDao;
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

	private List<ThingEntity> findAll() {
		final List<ThingEntity> list = new ArrayList<>();
		thingDao.findAll().forEach(thing -> {
			list.add(thing);
		});
		return list;
	}

	@Override
	public void delete(final int thingId) {
		stringAttributeDao.deleteByThingId(thingId);
		integerAttributeDao.deleteByThingId(thingId);
		booleanAttributeDao.deleteByThingId(thingId);
		dateTimeAttributeDao.deleteByThingId(thingId);
		fileAttributeDao.deleteByThingId(thingId);
		floatAttributeDao.deleteByThingId(thingId);
		thingDao.deleteById(thingId);
		wordService.deleteIndex(thingId);
	}

	@Override
	public ThingEntity findById(final int thingId) {
		final Optional<ThingEntity> optional = thingDao.findById(thingId);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	private String getPresentation(final ThingEntity thing, final boolean includeType) {
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
		final Optional<AttrDefnEntity> optionalAttrDefn = attrDefnDao.findByTypeIdAndName(typeId,
			"name");
		if (optionalAttrDefn.isPresent()) {
			final AttrDefnEntity attrDefn = optionalAttrDefn.get();
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
		final List<ThingEntity> things = this.findAll();
		log.info("  thing count: " + things.size());
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
		final FindTypeResult type = typeService.findById(typeId);
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
		final Optional<AttrDefnEntity> optAttrDefn = attrDefnDao.findByTypeIdAndName(
			thing.getTypeId(), attrName);
		if (!optAttrDefn.isPresent()) {
			throw new AttributeNotFoundException("Attribute named '" + attrName + "' not found");
		}
		final AttrDefnEntity attrDefn = optAttrDefn.get();
		final String handlerName = attrDefn.getHandler();
		final Handler handler = handlerProvider.getHandler(handlerName);
		final Object valueObj = handler.getAttributeValue(thing.getId(), attrDefn.getId());
		return valueObj == null ? null : valueObj.toString();
	}

	public void saveAttributeValue(final ThingEntity thing, final String attrName,
			final Object attrValue) throws AttributeNotFoundException {
		final Optional<AttrDefnEntity> optAttrDefn = attrDefnDao.findByTypeIdAndName(
			thing.getTypeId(), attrName);
		if (!optAttrDefn.isPresent()) {
			throw new AttributeNotFoundException("Attribute named '" + attrName + "' not found");
		}
		final AttrDefnEntity attrDefn = optAttrDefn.get();
		final String handlerName = attrDefn.getHandler();
		final Handler handler = handlerProvider.getHandler(handlerName);
		handler.saveAttributeValue(thing.getId(), attrDefn.getId(), attrValue);
	}

	public void saveAttributeValue(final ThingEntity thing, final int attrDefnId,
			final Object attrValue) throws AttributeNotFoundException {
		final Optional<AttrDefnEntity> optAttrDefn = attrDefnDao.findById(attrDefnId);
		if (!optAttrDefn.isPresent()) {
			throw new AttributeNotFoundException("Attribute '" + attrDefnId + "' not found");
		}
		final AttrDefnEntity attrDefn = optAttrDefn.get();
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
}
