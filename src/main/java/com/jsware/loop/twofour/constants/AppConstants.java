package com.jsware.loop.twofour.constants;

import java.util.Date;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.jsware.loop.twofour.model.Contest;
import com.jsware.loop.twofour.model.Submission;
import com.jsware.loop.twofour.model.SubmissionTicket;

@Component
public class AppConstants {

	public Contest activeContest;

	public Contest previousContest;

	private int defaulk = 10;
	private int count;

	public AppConstants() {
		refresh();
	}

	public void refresh() {
		count = 0;
	}

	public SubmissionTicket submit(Submission sub) {
		activeContest.sub_count++;
		String backupSlot = null;
		if (count < 5) {
			backupSlot = ("backup" + count + new Date() + sub.content_extension);
			backupSlot = cleanURL(backupSlot);

			sub.content_url = backupSlot;
			sub.contest = activeContest;

			activeContest.backups.add(sub);
			sub.member.getSubmissions().add(sub);

			count++;
		}

		String winner = null;

		for (int i = 0; i < sub.rolls; i++) {
			int factor = new Random().nextInt(defaulk);

			if (factor == 1) {
				winner = ("winner" + new Date() + sub.content_extension);
				winner = cleanURL(winner);
				sub.content_url = winner;
				activeContest.loadSubmission(sub);
				break;
			}

		}

		return new SubmissionTicket(winner, backupSlot);
	}

	private String cleanURL(String url) {

		url = url.replace(" ", "");
		url = url.replace(":", "");
		return url;
	}

}
