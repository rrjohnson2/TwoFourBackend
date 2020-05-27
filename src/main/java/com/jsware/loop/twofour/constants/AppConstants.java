package com.jsware.loop.twofour.constants;



import java.util.Calendar;
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
	
	private Submission[] backups;
	
	private int defaulk =10;
	private int count;
	
	public AppConstants() {
		refresh();
	}
	
	public void refresh()
	{
		backups = new Submission[5];
		count = 0;
	}
	
	
	public SubmissionTicket submit(Submission sub)
	{
		String backupSlot = null;
		if(count < backups.length)
		{
			backupSlot = ("backup"+count + new Date()+sub.content_extension);
			backupSlot = cleanURL(backupSlot);
			backups[count] = sub;
			count++;
		}
		
		String winner = null;
		
		for (int i = 0; i < sub.rolls; i++) {
			Random rand = new Random();
			int factor = rand.nextInt(defaulk); 
			
			if(factor == 1 ) {
				winner= ("winner"+ new Date()+sub.content_extension);
				winner = cleanURL(winner);
				sub.content_url = winner;
				activeContest.loadSubmission(sub);
				break;
			};
		}
		
		return new SubmissionTicket(winner,backupSlot);
	}
	
	private String  cleanURL(String url) {
		
		url = url.replace(" ", "");
		url = url.replace(":", "");
		return url;
	}
	

}
