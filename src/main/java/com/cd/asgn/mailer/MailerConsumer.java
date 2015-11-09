package com.cd.asgn.mailer;

import java.util.concurrent.Callable;

public class MailerConsumer implements Callable<Integer>, Runnable {

	private Integer mail_id;
	
	public void run() {
		// TODO Auto-generated method stub
		
	}

	public Integer call() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer getMail_id() {
		return mail_id;
	}

	public void setMail_id(Integer mail_id) {
		this.mail_id = mail_id;
	}
	

}
