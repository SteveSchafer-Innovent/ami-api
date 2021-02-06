package com.stephenschafer.ami.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "misspellings")
@Getter
@Setter
@ToString
public class MisspellingsEntity {
	@Id
	@Column
	private String incorrect;
	@Column
	private String correct;
}
