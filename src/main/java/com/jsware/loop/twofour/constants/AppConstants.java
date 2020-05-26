package com.jsware.loop.twofour.constants;



import java.util.Calendar;
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
		int backupSlot =-1;
		if(count < backups.length)
		{
			backupSlot = count;
			backups[count] = sub;
			count++;
		}
		
		boolean winner = false;
		
		for (int i = 0; i < sub.rolls; i++) {
			Random rand = new Random();
			int factor = rand.nextInt(defaulk); 
			
			if(factor == 1 ) {
				winner= true;
				activeContest.loadSubmission(sub);
				break;
			};
		}
		
		return new SubmissionTicket(winner,backupSlot);
	}

}
