package com.cd.asgn.mailer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class MailSender {

	public static void main(String[] args) throws ClassNotFoundException {
		Properties property = new Properties();
		InputStream input = null;
		
		try {
			// Properties File
			input = new FileInputStream("src/main/resources/config.properties");          
			property.load(input);
		}catch (IOException ex) {
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
		// Database Driver
		Class.forName(property.getProperty("database_driver"));

	    Connection connection = null;
	    try
	    {
	      // creating database connection
	      connection = DriverManager.getConnection(property.getProperty("database_connection"));
	      Statement statement = connection.createStatement();
	      
	      // Fetching data from database
	      ResultSet result_set = statement.executeQuery("select * from emailqueue");
	      
	      // Iterating data read from database
	      while(result_set.next())
	      {
	    	System.out.println("ID : " + result_set.getInt("id"));
	    	System.out.println("From Email : " + result_set.getString("from_email_address"));
	    	System.out.println("To Email : " + result_set.getString("to_email_address"));
	    	System.out.println("Subject : " + result_set.getString("subject"));
	    	System.out.println("Body : " + result_set.getString("body"));
	      }
	    }
	    catch(SQLException e)
	    {
	    	System.err.println(e.getMessage());
	    }
	    finally
	    {
	      try
	      {
	        if(connection != null)
	          connection.close();
	      }
	      catch(SQLException e)
	      {
	    	  System.err.println(e);
	      }
	    }
	  }
}
