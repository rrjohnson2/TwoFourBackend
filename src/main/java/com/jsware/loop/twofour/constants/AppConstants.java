package com.jsware.loop.twofour.constants;

import org.springframework.stereotype.Component;

import com.jsware.loop.twofour.model.Contest;
import com.jsware.loop.twofour.model.Submission;

@Component
public class AppConstants {

	public Contest activeContest;

	public Contest previousContest;

	public void submit(Submission sub) {
		activeContest.sub_count++;
		activeContest.getSubs().add(sub);
	}
}
