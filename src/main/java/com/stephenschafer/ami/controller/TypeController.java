package com.stephenschafer.ami.controller;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

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

import com.stephenschafer.ami.jpa.FindTypeResult;
import com.stephenschafer.ami.jpa.TypeEntity;
import com.stephenschafer.ami.service.TypeService;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class TypeController {
	@Autowired
	private TypeService typeService;
	@PersistenceContext
	private EntityManager entityManager;

	@PostMapping("/type")
	public ApiResponse<TypeEntity> insert(@RequestBody final TypeEntity type) {
		log.info("POST /type " + type);
		TypeEntity typeEntity;
		try {
			typeEntity = typeService.insert(type);
		}
		catch (final Exception e) {
			log.error("Failed to insert type", e);
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"Failed to insert type: " + e.getMessage(), null);
		}
		return new ApiResponse<>(HttpStatus.OK.value(), "Type saved successfully.", typeEntity);
	}

	@PutMapping("/type")
	public ApiResponse<TypeEntity> update(@RequestBody final TypeEntity type) {
		log.info("PUT /type " + type);
		TypeEntity typeEntity;
		try {
			typeEntity = typeService.update(type);
		}
		catch (final Exception e) {
			log.error("Failed to update type", e);
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"Failed to update type: " + e.getMessage(), null);
		}
		return new ApiResponse<>(HttpStatus.OK.value(), "Type saved successfully.", typeEntity);
	}

	@DeleteMapping("/type/{id}")
	public ApiResponse<Void> delete(@PathVariable final int id) {
		log.info("DELETE /type/" + id);
		try {
			typeService.delete(id);
		}
		catch (final Exception e) {
			log.error("Failed to delete type", e);
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"Failed to delete type: " + e.getMessage(), null);
		}
		return new ApiResponse<>(HttpStatus.OK.value(), "Type deleted successfully", null);
	}

	@Getter
	@Setter
	@ToString
	public static class ListTypeResponse implements Serializable {
		private static final long serialVersionUID = 1L;
		private int id;
		private String name;
		private int thingCount;
		private int attrCount;
		private int sourceLinkCount;
		private int targetLinkCount;
	}

	@GetMapping("/types")
	public ApiResponse<List<ListTypeResponse>> list() {
		log.info("GET /types");
		final List<ListTypeResponse> list = new ArrayList<ListTypeResponse>();
		typeService.findAll().iterator().forEachRemaining(typeEntity -> {
			final ListTypeResponse listTypeEntity = new ListTypeResponse();
			final int typeId = typeEntity.getId();
			listTypeEntity.setId(typeId);
			listTypeEntity.setName(typeEntity.getName());
			listTypeEntity.setThingCount(getThingCount(typeId).intValue());
			listTypeEntity.setAttrCount(getAttrCount(typeId).intValue());
			listTypeEntity.setTargetLinkCount(getTargetLinkDefnCount(typeId).intValue());
			list.add(listTypeEntity);
		});
		return new ApiResponse<>(HttpStatus.OK.value(), "Types listed successfully.", list);
	}

	private final static String GET_ATTRDEFN_COUNT = "AttrDefnEntity.getAttrDefnCount";

	private BigInteger getAttrCount(final int typeId) {
		return getCount(GET_ATTRDEFN_COUNT, typeId);
	}

	private final static String GET_THING_COUNT = "ThingEntity.getThingCount";

	private BigInteger getThingCount(final int typeId) {
		return getCount(GET_THING_COUNT, typeId);
	}

	private final static String GET_TARGET_LINKDEFN_COUNT = "LinkDefnEntity.getTargetLinkDefnCount";

	private BigInteger getTargetLinkDefnCount(final int typeId) {
		return getCount(GET_TARGET_LINKDEFN_COUNT, typeId);
	}

	private BigInteger getCount(final String queryName, final int typeId) {
		final Query namedQuery = entityManager.createNamedQuery(queryName);
		namedQuery.setParameter("typeId", typeId);
		final List<?> resultList = namedQuery.getResultList();
		for (final Object resultItem : resultList) {
			if (resultItem instanceof BigInteger) {
				return (BigInteger) resultItem;
			}
			log.error("Object returned from " + queryName + " was not a BigInteger.  It was "
				+ resultItem.getClass().getName());
		}
		log.error("Result list returned from " + queryName + " was empty");
		return BigInteger.valueOf(0L);
	}

	@GetMapping("/type/{id}")
	public ApiResponse<FindTypeResult> get(@PathVariable final Integer id) {
		log.info("GET /type/" + id);
		return new ApiResponse<>(HttpStatus.OK.value(), "Type gotten successfully.",
				typeService.findById(id));
	}
}
