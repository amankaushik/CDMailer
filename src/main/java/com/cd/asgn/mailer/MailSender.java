package com.cd.asgn.mailer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import com.cd.asgn.mailer.EmailStructure;

public class MailSender {
	static Properties property = new Properties();
	static InputStream input = null;
	public static final int THREAD_POOL_SIZE = 5;
	final static ExecutorService mailConsumers = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
	final static Logger logger = Logger.getLogger(MailSender.class);

	/* Convert mail data to MimeMessage objects */
	public static Map<Integer, MimeMessage> get_all_messages(Map<Integer, EmailStructure> mails_to_send,
			Session session) {

		Map<Integer, MimeMessage> messages = new HashMap<Integer, MimeMessage>();

		for (Entry<Integer, EmailStructure> entry : mails_to_send.entrySet()) {
			MimeMessage message = new MimeMessage(session);
			try {
				message.addHeader("Content-type", "text/HTML; charset=UTF-8");
				message.addHeader("format", "flowed");
				message.addHeader("Content-Transfer-Encoding", "8bit");
				message.setFrom(new InternetAddress(entry.getValue().getFrom_email()));
				message.setSubject(entry.getValue().getSubject(), "UTF-8");
				message.setText(entry.getValue().getBody(), "UTF-8");
				message.setSentDate(new Date());
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(entry.getValue().getTo_email()));
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			messages.put(entry.getKey(), message);
		}

		return messages;
	}

	/* Gets mails data, starts threads and send mails */

	public void startSending() throws ClassNotFoundException, MessagingException {
		logger.info("Starting mail sending process ..");
		/* Details of mails to send from the database */
		Map<Integer, EmailStructure> mailsToSend = new HashMap<Integer, EmailStructure>();
		DataUtil dataUtil = new DataUtil();
		mailsToSend = dataUtil.get_email_data(property, input, logger);

		while (!mailsToSend.isEmpty()) {
			logger.info("Sending mail ..");

			/* IDs of mails that failed during sending */
			ArrayList<FutureTask<Integer>> mailsNotSent = new ArrayList<FutureTask<Integer>>();
			Session session = Session.getInstance(property, null);

			/* Details of mails as MimeMessage object */
			Map<Integer, MimeMessage> messages = new HashMap<Integer, MimeMessage>();

			messages = get_all_messages(mailsToSend, session);

			for (Entry<Integer, MimeMessage> entry : messages.entrySet()) {
				MimeMessage mimeMessage = new MimeMessage(session);
				mimeMessage = entry.getValue();
				mimeMessage.saveChanges();
				MailerConsumer mailerConsumer = new MailerConsumer(mimeMessage, entry.getKey(), property, session);
				FutureTask<Integer> futureTask = new FutureTask<Integer>(mailerConsumer);
				MailSender.mailConsumers.execute(mailerConsumer);
				MailSender.mailConsumers.submit(futureTask);
				mailsNotSent.add(futureTask);
			}

			/* IDs of mails not send */
			ArrayList<Integer> badID = new ArrayList<>();

			/*
			 * Iterate through FutureTask and add IDs of failed mails to badID
			 * ArrayList
			 */
			for (FutureTask<Integer> mail : mailsNotSent) {
				try {
					if(mail.get() != -1)
						badID.add(mail.get());
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}

			/* Change 'processed' status of failed mails back to 0 */
			if (!badID.isEmpty()) {
				logger.info("Changing processed status of messages not sent");
				Class.forName(property.getProperty("database_driver"));
				Connection connection = null;
				try {
					connection = DriverManager.getConnection(property.getProperty("database_connection"));
					/*
					 * get ECXCLUSIVE LOCK, so that only one transaction reads
					 * and writes at one time
					 */
					boolean get_lock = connection.createStatement().execute("PRAGMA locking_mode = EXCLUSIVE");
					/* wait till lock is acquired */
					while (!get_lock) {
					}
					PreparedStatement preparedStatement = connection
							.prepareStatement("UPDATE emailqueue SET processed = 0 WHERE id = ?");
					for (Integer id : badID) {
						preparedStatement.setInt(1, id);
						preparedStatement.executeUpdate();
					}
				} catch (SQLException e) {
					System.err.println(e.getMessage());
				} finally {
					try {
						if (connection != null)
							connection.close();
					} catch (SQLException e) {
						System.err.println(e);
					}
				}
			}
			mailsToSend = dataUtil.get_email_data(property, input, logger);
		}
		/* Shutdown the threadpool */
		mailConsumers.shutdown();
		try {
			/* await shutdown of all threads currently working */
			mailConsumers.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
			throws ClassNotFoundException, MessagingException, UnsupportedEncodingException, InterruptedException {
		try {
			/* Properties File */
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
		MailSender mailSender = new MailSender();
		/* Start sending mails */
		mailSender.startSending();
	}
}