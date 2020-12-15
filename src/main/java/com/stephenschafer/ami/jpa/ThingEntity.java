package com.stephenschafer.ami.jpa;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "thing")
@Getter
@Setter
@ToString
@NamedNativeQueries({
	@NamedNativeQuery(name = "ThingEntity.getThingCount", query = "SELECT count(*) FROM thing t WHERE t.type_id=:typeId") })
public class ThingEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column
	private int creator;
	@Column
	private Date created;
	@Column(name = "type_id")
	private int typeId;
}