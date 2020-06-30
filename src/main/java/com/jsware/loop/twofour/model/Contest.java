package com.jsware.loop.twofour.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@SequenceGenerator(name = "con_seq", initialValue = 1)
public class Contest {

	@Id
	@JsonIgnore
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "con_seq")
	private long id;

	@OneToMany(mappedBy = "contest", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@JsonIgnore
	private List<Submission> subs = new ArrayList<Submission>();

	private Calendar calendar = Calendar.getInstance();
	public int sub_count = 0;

	public int winning_index;

	@SuppressWarnings("deprecation")
	public Contest() {
		Date today = new Date();
		calendar.setTime(new Date());
		calendar.add(Calendar.MINUTE, 1);
		sub_count = 0;
	}

	public void loadSubmission(int index) {
		winning_index = index;
	}

	public void nullify() {
		winning_index = -1;
	}

	public long getId() {
		return id;
	}

	public List<Submission> getSubs() {
		return subs;
	}

	public Calendar getCalendar() {
		return calendar;
	}

	public int getSub_count() {
		return sub_count;
	}

	public int getWinning_index() {
		return winning_index;
	}

}
