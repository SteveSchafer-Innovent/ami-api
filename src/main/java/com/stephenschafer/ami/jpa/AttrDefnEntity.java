package com.stephenschafer.ami.jpa;

import java.io.Serializable;

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
@Table(name = "attribute_defn")
@Getter
@Setter
@ToString
@NamedNativeQueries({
	@NamedNativeQuery(name = "AttrDefnEntity.getAttrDefnCount", query = "SELECT count(*) FROM attribute_defn WHERE type_id=:typeId") })
public class AttrDefnEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name = "name")
	private String name;
	@Column(name = "handler")
	private String handler;
	@Column(name = "type_id")
	private int typeId;
	@Column
	private boolean multiple;
	@Column(name = "show_in_list")
	private boolean showInList;
	@Column(name = "edit_in_list")
	private boolean editInList;
	@Column(name = "sort_order")
	private float sortOrder;
}
