package com.cd.asgn.mailer;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Properties;

public class DataUtil {
	HashMap<Integer, EmailStructure> get_email_data(Properties property, InputStream input) throws ClassNotFoundException {
		
		/*
		 * EmailStructure : Class to hold email information from the
		 * database ID to EmailStructure map
		 */
		HashMap<Integer, EmailStructure> mails_to_send = new HashMap<Integer, EmailStructure>();

		/* ----------------------------------- DATABASE CONNECTION -------------------------------------------------*/
		// Database Driver
		Class.forName(property.getProperty("database_driver"));

		Connection connection = null;
		try {
			// creating database connection
			connection = DriverManager.getConnection(property.getProperty("database_connection"));
			Statement statement = connection.createStatement();

			// Fetching data from database
			ResultSet result_set = statement.executeQuery("select * from emailqueue");

			// Iterating data read from database
			while (result_set.next()) {
				EmailStructure emailStructure = new EmailStructure();
				emailStructure.setFrom_email(result_set.getString("from_email_address"));
				emailStructure.setTo_email(result_set.getString("to_email_address"));
				emailStructure.setSubject(result_set.getString("subject"));
				emailStructure.setBody(result_set.getString("body"));
				mails_to_send.put(result_set.getInt("id"), emailStructure);
			}
		} catch (

		SQLException e)

		{
			System.err.println(e.getMessage());
		} finally

		{
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				System.err.println(e);
			}
		}
		return mails_to_send;
	}
}
