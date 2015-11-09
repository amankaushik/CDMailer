package com.cd.asgn.mailer;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class MailerProducer implements Runnable {
	
	public void get_email_from_database() {
		ArrayList<FutureTask<Integer>> mails_not_sent = new ArrayList<FutureTask<Integer>>();
		for(int i = 0; i < THREAD_POOL_SIZE; i++) {
			MailerConsumer mailerConsumer = new MailerConsumer();
			FutureTask<Integer> futureTask = new FutureTask<Integer>(mailerConsumer);
		}
	}

	public void run() {
		// TODO Auto-generated method stub
		
	}
}
