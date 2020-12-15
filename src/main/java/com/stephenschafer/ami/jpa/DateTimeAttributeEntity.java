package com.stephenschafer.ami.jpa;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "timestamp_attribute")
@Getter
@Setter
@ToString
@IdClass(AttributeId.class)
public class DateTimeAttributeEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "thing_id")
	private int thingId;
	@Id
	@Column(name = "attribute_defn_id")
	private int attrDefnId;
	@Column
	private Timestamp value;
}
