package com.cd.asgn.mailer;

import java.util.Properties;
import java.util.concurrent.Callable;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

public class MailerConsumer implements Callable<Integer>, Runnable {

	private Integer mail_id;
	private Session session;
	private Properties property;
	public MailerConsumer(MimeMessage mimeMessage, Integer id, Properties property2, Session session2) {
		// TODO Auto-generated constructor stub
	}

	public void run() {
		// TODO Auto-generated method stub
		
	}

	public Integer call() throws Exception {
		// TODO Auto-generated method stub
		Transport transport = session.getTransport(property.getProperty("protocol"));
		try {
			transport.connect(property.getProperty("mail.smtp.host"),
			Integer.parseInt(property.getProperty("mail.smtp.port")), property.getProperty("user"),	property.getProperty("password"));
			//transport.sendMessage(message, message.getAllRecipients());
		} catch (AuthenticationFailedException e) {
			System.out.println("Authentication Failed");
			e.printStackTrace();
		} catch (MessagingException e) {
			System.out.println("Something Else Failed");
			e.printStackTrace();
		} finally {
			transport.close();
		}

		//return future;
		return null;
	}

	public Integer getMail_id() {
		return mail_id;
	}

	public void setMail_id(Integer mail_id) {
		this.mail_id = mail_id;
	}
	

}