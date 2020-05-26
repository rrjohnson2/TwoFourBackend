package com.jsware.loop.twofour.model;



public class SubmissionTicket {

	
	public SubmissionTicket(boolean winner, int backupSlot2) {
		this.win = winner;
		this.backupSlot = backupSlot2;
	}

	public boolean	 win;
	public int backupSlot =-1;
}
