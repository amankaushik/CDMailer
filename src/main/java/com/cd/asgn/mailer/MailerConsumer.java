package com.cd.asgn.mailer;

import java.util.Properties;
import java.util.concurrent.Callable;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

public class MailerConsumer implements Callable<Integer>, Runnable {

	private Integer mail_id;
	private Session session;
	private Properties property;
	private MimeMessage mimeMessage;
	/* to check if mail sending failed or not */
	private Integer flag = -1;
	
	public MailerConsumer(MimeMessage mimeMessage, Integer id, Properties property, Session session) {
		this.mail_id = id;
		this.property = property;
		this.session = session;
		this.mimeMessage = mimeMessage;
	}

	public void run() {
		Transport transport = null;
		try {
			/* initialize transport and authenticate */
			transport = session.getTransport(property.getProperty("protocol"));
			transport.connect(property.getProperty("mail.smtp.host"),
			Integer.parseInt(property.getProperty("mail.smtp.port")), property.getProperty("user"),	property.getProperty("password"));
			transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
		} catch (NoSuchProviderException e1) {
			flag = 0; // program should not stop.
			e1.printStackTrace();
		} catch (AuthenticationFailedException e) {
			flag = 0; // program should not stop.
			System.out.println("Authentication Failed");
			e.printStackTrace();
		} catch (MessagingException e) {
			flag = 0; // program should not stop.
			System.out.println("Something Else Failed");
			e.printStackTrace();
		} finally {
			try {
				transport.close();
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
	}

	public Integer call() throws Exception {
		if(flag != -1) {
			return this.mail_id;
		} 
		else {
			return flag;
		}
	}

	public Integer getMail_id() {
		return mail_id;
	}

	public void setMail_id(Integer mail_id) {
		this.mail_id = mail_id;
	}
	

}