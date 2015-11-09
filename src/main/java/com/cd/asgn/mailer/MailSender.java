package com.cd.asgn.mailer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import com.cd.asgn.mailer.EmailStructure;

public class MailSender {
	static Properties property = new Properties();
	static InputStream input = null;
	public static final int THREAD_POOL_SIZE = 5;
	final static ExecutorService mailProducers = Executors.newFixedThreadPool(1);
	final static ExecutorService mailConsumers = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
	
	public static ArrayList<MimeMessage> get_all_messages(HashMap<Integer, EmailStructure> mails_to_send,
			Session session) throws MessagingException, UnsupportedEncodingException {
		ArrayList<MimeMessage> messages = new ArrayList<MimeMessage>();
		Iterator<Entry<Integer, EmailStructure>> itr = mails_to_send.entrySet().iterator();
		while (itr.hasNext()) {
			EmailStructure emailStructure = new EmailStructure();

			HashMap.Entry<Integer, EmailStructure> pair = (HashMap.Entry<Integer, EmailStructure>) itr.next();
			emailStructure = (EmailStructure) pair.getValue();
			MimeMessage message = new MimeMessage(session);

			message.addHeader("Content-type", "text/HTML; charset=UTF-8");
			message.addHeader("format", "flowed");
			message.addHeader("Content-Transfer-Encoding", "8bit");
			message.setFrom(new InternetAddress(emailStructure.getFrom_email(), "NoReply-JD"));
			message.setReplyTo(InternetAddress.parse(emailStructure.getFrom_email(), false));
			message.setSubject(emailStructure.getSubject(), "UTF-8");
			message.setText(emailStructure.getBody(), "UTF-8");
			message.setSentDate(new Date());
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailStructure.getTo_email(), false));
			System.out.println("Message Created");
			messages.add(message);
		}
		return messages;
	}

	public static void main(String[] args)
			throws ClassNotFoundException, MessagingException, UnsupportedEncodingException, InterruptedException {
		try {
			// Properties File
			input = new FileInputStream("src/main/resources/config.properties");
			property.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		HashMap<Integer, EmailStructure> mails_to_send = new HashMap<Integer, EmailStructure>();
		DataUtil dataUtil = new DataUtil();
		mails_to_send = dataUtil.get_email_data(property, input);
		while(!mails_to_send.isEmpty()) {
			
			mails_to_send = dataUtil.get_email_data(property, input);
			
			/* 
			 * Handle rejected mails
			 * */
		}
		mailProducers.shutdown();
		mailProducers.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		mailConsumers.shutdown();
		mailConsumers.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		/*
		MailSender mailSender = new MailSender();
		for(int i = 0; i < THREAD_POOL_SIZE; i++) {
			mailerProducers.submit(mailSender )
		}
		*/
		/*

		///* ------------------------------------- MAIL SENDER ------------------------------------------------- 

		System.out.println("Start Sending");
		Session session = Session.getInstance(property, null);
		ArrayList<MimeMessage> messages = new ArrayList<MimeMessage>();
		messages = get_all_messages(mails_to_send, session);
		Transport transport = session.getTransport(property.getProperty("protocol"));
		transport.connect(property.getProperty("mail.smtp.host"),
				Integer.parseInt(property.getProperty("mail.smtp.port")), property.getProperty("user"),
				property.getProperty("password"));
		try {
			for (Message message : messages) {
				message.saveChanges();
				transport.sendMessage(message, message.getAllRecipients());
				System.out.println("Message Sent.");
			}
		} catch (AuthenticationFailedException e) {
			System.out.println("Authentication Failed");
			e.printStackTrace();
		} catch (MessagingException e) {
			System.out.println("Something Else Failed");
			e.printStackTrace();
		} finally {
			transport.close();
		}
		*/
		
		
		
	}
	
}
