package com.cd.asgn.mailer;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

public class DataUtil {
	
	/* Get mail details from database */
	Map<Integer, EmailStructure> get_email_data(Properties property, InputStream input, Logger logger)
			throws ClassNotFoundException {
		/*
		 * EmailStructure : Class to hold email information from the database ID
		 * to EmailStructure map
		 */
		Map<Integer, EmailStructure> mails_to_send = new HashMap<Integer, EmailStructure>();

		// Database Driver
		Class.forName(property.getProperty("database_driver"));

		Connection connection = null;
		try {
			// creating database connection
			logger.info("Establishing Database Connection ..");
			connection = DriverManager.getConnection(property.getProperty("database_connection"));
			/*
			 * get ECXCLUSIVE LOCK, so that only one transaction reads and
			 * writes at one time
			 */
			logger.info("Getting EXCLUSIVE lock ..");
			boolean get_lock = connection.createStatement().execute("PRAGMA locking_mode = EXCLUSIVE");
			/* wait till lock is acquired */
			while (!get_lock) {
			}
			logger.info("Lock acquired ..");
			Statement statement = connection.createStatement();
			String selectQuery = "SELECT * FROM emailqueue WHERE processed = 0 LIMIT " + property.getProperty("query_limit"); 
			ResultSet result_set = statement.executeQuery(selectQuery); 
			
			while (result_set.next()) {
				EmailStructure emailStructure = new EmailStructure();
				emailStructure.setFrom_email(result_set.getString("from_email_address"));
				emailStructure.setTo_email(result_set.getString("to_email_address"));
				emailStructure.setSubject(result_set.getString("subject"));
				emailStructure.setBody(result_set.getString("body"));
				mails_to_send.put(result_set.getInt("id"), emailStructure);
			}
			logger.info("Results fetched, updating status ..");
		/* Update 'processed' status of read(above) records to 1 */ 
		PreparedStatement preparedStatement = connection.prepareStatement("UPDATE emailqueue SET processed = 1 WHERE id = ?");
		
		for(Integer id : mails_to_send.keySet()) {
			preparedStatement.setInt(1, id);
			preparedStatement.executeUpdate();
		}
		
		} catch (SQLException e) {
			logger.error(e.getMessage());
			System.err.println(e.getMessage());
		} finally {
			try {
				if (connection != null)
					logger.info("Closing Database Connection ..");
					connection.close();
			} catch (SQLException e) {
				logger.error(e.getMessage());
				System.err.println(e);
			}
		}
		return mails_to_send;
	}
}