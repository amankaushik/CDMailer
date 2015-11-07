package com.cd.asgn.mailer;

public class EmailStructure {
	String to_email;
	String from_email;
	String subject;
	String body;
	/**
	 * @return the to_email
	 */
	public String getTo_email() {
		return to_email;
	}
	/**
	 * @param to_email the to_email to set
	 */
	public void setTo_email(String to_email) {
		this.to_email = to_email;
	}
	/**
	 * @return the from_email
	 */
	public String getFrom_email() {
		return from_email;
	}
	/**
	 * @param from_email the from_email to set
	 */
	public void setFrom_email(String from_email) {
		this.from_email = from_email;
	}
	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}
	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}
	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}
	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}
	
}
