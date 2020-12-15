package com.stephenschafer.ami.jpa;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "float_attribute")
@Getter
@Setter
@ToString
@IdClass(AttributeId.class)
public class FloatAttributeEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "thing_id")
	private int thingId;
	@Id
	@Column(name = "attribute_defn_id")
	private int attrDefnId;
	@Column
	private Double value;
}
