package com.jsware.loop.twofour.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@SequenceGenerator(name = "mem_seq", initialValue = 1)
public class Submission {

	public Submission(Submission sub) {
		this.content_url = sub.content_url;
		this.description = sub.description;
		this.member = sub.member;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mem_seq")
	public long id;

	public Submission() {
	}

	public String description;

	@ManyToOne
	public Member member;

	@ManyToOne
	@JsonIgnore
	public Contest contest;
	public String content_url;
	public String content_type;
	public String content_extension;

}
