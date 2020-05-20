package com.jsware.loop.twofour.constants;

import java.util.Calendar;
import java.util.Date;

public class AppConstants {

	public Calendar calendar = Calendar.getInstance();
	public Date contestTime =new Date();
	
	public AppConstants() {
		calendar.setTime(contestTime);
		nextContestTime();
	}
	
	public void nextContestTime()
	{
		calendar.set(contestTime.getYear(), contestTime.getMonth(), contestTime.getDate()+1, 12, 0, 0);
		contestTime = calendar.getTime();
	}

}
