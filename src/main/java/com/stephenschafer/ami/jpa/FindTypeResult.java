package com.stephenschafer.ami.jpa;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class FindTypeResult {
	private int id;
	private String name;
	private List<Map<String, Object>> attrdefns;

	@JsonIgnore
	public TypeEntity getTypeEntity() {
		final TypeEntity typeEntity = new TypeEntity();
		typeEntity.setId(this.id);
		typeEntity.setName(this.name);
		return typeEntity;
	}
}
