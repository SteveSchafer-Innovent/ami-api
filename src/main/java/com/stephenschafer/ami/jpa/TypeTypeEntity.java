package com.stephenschafer.ami.jpa;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "type_type")
@Getter
@Setter
@EqualsAndHashCode
public class TypeTypeEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "type_id")
	private int typeId;
	@Id
	@Column(name = "super_type_id")
	private int superTypeId;
}
