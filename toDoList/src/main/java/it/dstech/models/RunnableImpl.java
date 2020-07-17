package it.dstech.models;

import javax.mail.MessagingException;

import it.dstech.service.MailService;

public class RunnableImpl implements Runnable {
	
	private MailService mail;
	
	private Activity activity;
	
	public RunnableImpl(Activity activity, MailService mail) {
		this.activity = activity;
		this.mail = mail;
	}

	@Override
	public void run() {
		
		User currUser = activity.getUser();
		try {
			mail.sendMail(currUser.getEmail(), "Reminder " + activity.getActivityTitle(), "This activity will expired in 30 minutes.");
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
	}


}
