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
	
	public String content_type;
	
	@SuppressWarnings("deprecation")
	public Contest() {
		Date today = new Date();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 12);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		sub_count = 0;
	}
	
	
}