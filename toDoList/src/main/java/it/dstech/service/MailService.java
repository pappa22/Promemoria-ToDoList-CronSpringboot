package it.dstech.service;

import javax.mail.MessagingException;

public interface MailService {
	
	public void sendMail(String mailAddressee, String mailObject, String message) throws MessagingException;

}
