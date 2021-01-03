package com.stephenschafer.ami.jpa;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "user_type_context_thing")
@IdClass(UserTypeContextThingId.class)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserTypeContextThingEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "user_id")
	private int userId;
	@Id
	@Column(name = "type_id")
	private int typeId;
	@Id
	@Column(name = "context_thing_id")
	private int contextThingId;
	@Id
	@Column(name = "thing_id")
	private int thingId;
	@Column(name = "sort_order")
	private int sortOrder;
}
