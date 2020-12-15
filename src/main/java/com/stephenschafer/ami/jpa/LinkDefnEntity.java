package com.stephenschafer.ami.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "link_defn")
@Getter
@Setter
@ToString
@NamedNativeQueries({
	@NamedNativeQuery(name = "LinkDefnEntity.getTargetLinkDefnCount", query = "SELECT count(*) FROM link_defn WHERE target_type_id=:typeId") })
public class LinkDefnEntity {
	@Id
	@Column(name = "attribute_defn_id")
	private int attributeDefnId;
	@Column(name = "target_type_id")
	private Integer targetTypeId;
}
