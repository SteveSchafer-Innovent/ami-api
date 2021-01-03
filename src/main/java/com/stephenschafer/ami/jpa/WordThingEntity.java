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
@Table(name = "word_thing")
@IdClass(WordThingId.class)
@Getter
@Setter
@ToString
public class WordThingEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "word_id")
	private int wordId;
	@Id
	@Column(name = "attribute_defn_id")
	private int attrdefnId;
	@Id
	@Column(name = "thing_id")
	private int thingId;
}
