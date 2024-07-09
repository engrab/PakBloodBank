package com.pakbloodbank.items;

public class ItemCallRate {
	String subject, feedback, donated, id;

	public ItemCallRate(String id, String subject, String feedback, String donated) {
		this.id = id;
		this.subject = subject;
		this.donated = donated;
		this.feedback = feedback;
	}

	public String getId() {
		return id;
	}

	public String getSubject() {
		return subject;
	}

	public String getFeedback() {
		return feedback;
	}

	public String getDonated() {
		return donated;
	}
}
