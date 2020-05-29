package com.jsware.loop.twofour.model;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@SequenceGenerator(name="con_seq", initialValue=1)
public class Contest {
	
	
	@Id
	@JsonIgnore
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="con_seq")
	private long id;
	@ManyToOne
	public Member winner;
	
	public Calendar calendar = Calendar.getInstance();
	public int sub_count = 0;
	
	public String winning_description;
	
	public String winning_content_url;
	
	public String winning_content_type;
	
	
	@SuppressWarnings("deprecation")
	public Contest() {
		Date today = new Date();
		calendar.setTime(new Date());
		calendar.add(Calendar.SECOND, 45);
		sub_count = 0;
	}
	
	public void loadSubmission(Submission sub)
	{
		winning_description = sub.description;
		winner = sub.member;
		winning_content_url = sub.content_url;
		winning_content_type =  sub.content_type;
	}
	public void nullify()
	{
		winning_description = null;
		winner = null;
		winning_content_url =null;
		winning_content_type = null;
	}
	
}
