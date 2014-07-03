package com.claymus;

import java.util.Properties;
import java.util.logging.Level;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.google.apphosting.api.ApiProxy.OverQuotaException;

/*
 * Quota Details: http://code.google.com/appengine/docs/quotas.html#Mail
 * .--------------------.--------------------.------------------------.
 * |					| Free				 | Billing Enabled		  |
 * | Resource			|-------.------------|-----------.------------|
 * |					| Daily	| Per Minute | Daily	 | Per Minute |
 * |--------------------|-------|------------|-----------|------------|
 * | Mail API Calls		| 7,000 |		  32 | 1,700,000 |		4,900 |
 * | Recipients Emailed | 2,000 |		   8 | 7,400,000 |		5,100 |
 * | Admins Emailed		| 5,000 |		  24 | 3,000,000 |		9,700 |
 * | Attachments Sent	| 2,000 |		   8 | 2,900,000 |		8,100 |
 * '--------------------'-------'------------'-----------'------------'
 */

public class Mail {

	private String from;
	private String to;
	private String subject;
	private String body;

	/*
	 * Constructor(s)
	 */

	public Mail(String from, String to, String subject, String body) {
		this.from = from;
		this.to = to;
		this.subject = subject;
		this.body = body;
	}

	/*
	 * Getter(s) & Setter(s)
	 */

	public String getFrom() {
		return this.from;
	}

	public String getTo() {
		return this.to;
	}

	public String getSubject() {
		return this.subject;
	}

	public String getBody() {
		return this.body;
	}

	/*
	 * Helper Methods
	 */

	public boolean send() {
		Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        try {
        	MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(this.getFrom()));
            msg.addRecipient(
            		MimeMessage.RecipientType.TO,
            		new InternetAddress(this.getTo())
            	);
            msg.setSubject(this.getSubject());
            msg.setText(this.getBody());
            Transport.send(msg);
        } catch (MessagingException ex) {
        	Logger.get().log(Level.WARNING, null, ex);
        	return false;
        } catch (OverQuotaException ex) {
        	Logger.get().log(Level.SEVERE, null, ex);
        	return false;
        }
		return true;
	}

}
