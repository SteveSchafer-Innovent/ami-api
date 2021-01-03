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
@Table(name = "user_type")
@IdClass(UserTypeId.class)
@Getter
@Setter
@ToString
public class UserTypeEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "user_id")
	private int userId;
	@Id
	@Column(name = "type_id")
	private int typeId;
	@Column(name = "sort_order")
	private int sortOrder;
}
