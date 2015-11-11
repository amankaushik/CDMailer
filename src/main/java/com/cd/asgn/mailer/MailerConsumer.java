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
			System.out.println("Sending From : " + mimeMessage.getFrom()[0].toString() + " Sending To : " + mimeMessage.getAllRecipients()[0].toString());
			transport.connect(property.getProperty("mail.smtp.host"),
			Integer.parseInt(property.getProperty("mail.smtp.port")), property.getProperty("user"),	property.getProperty("password"));
			transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
			Thread.sleep(30);
		} catch (NoSuchProviderException e) {
			flag = 0; 
			e.printStackTrace();
		} catch (AuthenticationFailedException e) {
			flag = 0; 
			System.out.println("Authentication Failed");
			//e.printStackTrace();
		} catch (MessagingException e) {
			flag = 0; 
			System.out.println("Messaging Failed");
			//e.printStackTrace();
		} catch (InterruptedException e) {
			flag = 0;
			System.out.println("Interrupted");
			//e.printStackTrace();
		} finally {
			try {
				transport.close();
			} catch (MessagingException e) {
				//System.out.println("In Finally");
				e.printStackTrace();
			}
		}
	}
	public Integer call() throws Exception {
		if(flag == 0) {
			return this.mail_id;
		} 
		else {
			return flag;
		}
	}
}