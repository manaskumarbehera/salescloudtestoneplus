package dk.jyskit.waf.application.services.sendmail;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

import dk.jyskit.waf.application.JITWicketApplication;

/**
 * Send email using SMTP server configured in waf configuration
 * ([env].properties) Here is a typical example: mail.smtp.user=admin
 * mail.smtp.password=pw mail.smtp.auth=true mail.smtp.host=mail.mydomain.dk
 * mail.smtp.port=25
 *
 * Mails are sent like in this example (see more in MailTest) : MailResult
 * result = new Mail() .withSubject(subject) .withPlainText(text)
 * .withSender(sender) .withRecipient(john) .withRecipient(jane) .send();
 *
 * @author jan
 */
@Data
@Wither
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class Mail {

	private NameAndEmail sender;
	private List<NameAndEmail> recipients = new ArrayList<>();
	private String subject;
	private String plainText;
	private String html;
	private String encoding = "ISO-8859-1";
	private List<Attachment> attachments = new ArrayList<>();

	public Mail withRecipient(NameAndEmail recipient) {
		recipients.add(recipient);
		return this;
	}

	public Mail withAttachment(Attachment attachment) {
		attachments.add(attachment);
		return this;
	}
	
	public Mail plainTextFromHtml() { 
		if (html == null) {
			throw new IllegalArgumentException("HTML not yet defined?");
		}

		plainText = Jsoup.parse(
				html
					.replaceAll("(?i)<br[^>]*>", "br2n")
					.replace("</p>", "br2nbr2n</p>")
				).text();
		plainText = plainText
			.replaceAll("br2n ", "\n")
			.replaceAll("br2n", "\n");
		return this;
	}

	public MailResult send() {
		Properties mailProps = new Properties();
		Properties systemProperties = System.getProperties();
		Enumeration<Object> systemPropertyKeys = systemProperties.keys();
		
		String namespace = "unknown";
		try {
			namespace = JITWicketApplication.get().getNamespace();
		} catch (Exception e) {
			log.warn("Unknown namespace");
		}
		while (systemPropertyKeys.hasMoreElements()) {
			String key = (String) systemPropertyKeys.nextElement();
			if (key.startsWith(namespace + ".mail.smtp") || key.startsWith(namespace + ".mail.transport")) {
				String value = (String) systemProperties.get(key);
				mailProps.put(key.substring(namespace.length() + 1), value);
			}
		}

		List<NameAndEmail> goodRecipients = new ArrayList<>();
		List<NameAndEmail> badRecipients = new ArrayList<>();
		MailResult result = new MailResult(goodRecipients, badRecipients);

		NameAndEmail recipient = null;
		try {
			for (NameAndEmail r : recipients) {
				try {
					recipient = r;

					Session session = Session.getDefaultInstance(mailProps, null);
					MimeMessage msg = new MimeMessage(session);
					if (StringUtils.isEmpty(sender.getName())) {
						msg.setFrom(new InternetAddress(sender.getEmail(), true));
					} else {
						msg.setFrom(new InternetAddress(sender.getEmail(), sender.getName()));
					}

					msg.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient.getEmail(), true));
					msg.setSubject(subject);

					Multipart mp = new MimeMultipart();

					if (!StringUtils.isEmpty(html)) {
						MimeBodyPart part = new MimeBodyPart();
						part.setContent(html, "text/html; charset=" + encoding);
						mp.addBodyPart(part);
					}

					if (!StringUtils.isEmpty(plainText)) {
						MimeBodyPart part = new MimeBodyPart();
						part.setContent(plainText, "text/plain");
						mp.addBodyPart(part);
					}

					for (Attachment attachment : attachments) {
						MimeBodyPart part = new MimeBodyPart();
						part.setFileName(attachment.getFileName());
						part.setContent(attachment.getData(), attachment.getMimeType());
						mp.addBodyPart(part);
					}

					msg.setContent(mp);

					String protocol = mailProps.getProperty("mail.transport.protocol");
					if (StringUtils.isEmpty(protocol)) {
						protocol = "smtp";
						mailProps.setProperty("mail.transport.protocol", protocol);
					}
					Transport transport = session.getTransport(protocol);

					String host = mailProps.getProperty("mail.smtp.host");
					if (StringUtils.isEmpty(host)) {
						host = mailProps.getProperty("mail.smtps.host");
					}

					String userName = mailProps.getProperty("mail.smtp.user");
					if (StringUtils.isEmpty(userName)) {
						userName = mailProps.getProperty("mail.smtps.user");
					}

					String password = mailProps.getProperty("mail.smtp.password");
					if (StringUtils.isEmpty(password)) {
						password = mailProps.getProperty("mail.smtps.password");
					}

					if (StringUtils.isEmpty(userName)) {
						transport.connect();
					} else {
						transport.connect(host, userName, password);
					}
					transport.sendMessage(msg, msg.getAllRecipients());
					transport.close();
					goodRecipients.add(recipient);
				} catch (AddressException e) {
					badRecipients.add(recipient);
					log.error(e.getMessage());
				} catch (MessagingException e) {
					badRecipients.add(recipient);
					log.error(e.getMessage());
				}
			}
		} catch (UnsupportedEncodingException e) {
			badRecipients.add(recipient);
			log.error(e.getMessage());
		} catch (Exception e) {
			badRecipients.add(recipient);
			log.error(e.getMessage());
		}
		return result;
	}
	
	public static void main(String[] args) {
		// Put the following in environment file
//		System.setProperty("mail.smtp.user", "testbox-9ff8a8cedf2b7c40");
//		System.setProperty("mail.smtp.password", "1977b37f11637ae6");
//		System.setProperty("mail.smtp.auth", "true");
//		System.setProperty("mail.smtp.host", "mailtrap.io");
//		System.setProperty("mail.smtp.port", "2525");
		
		System.setProperty("unknown.mail.smtp.user", "jan@jyskit.dk");
		System.setProperty("unknown.mail.smtp.password", "29f81d29-9cc7-4600-8c55-bfd994d2db22");
		System.setProperty("unknown.mail.smtp.auth", "true");
		System.setProperty("unknown.mail.smtp.host", "smtp.mandrillapp.com");
		System.setProperty("unknown.mail.smtp.port", "587");

		MailResult result = new Mail()
			.withSubject("subject")
			// .withPlainText("text")
			.withHtml("<html><head></head><body>"
					+ "<a href='mpxapp://dk.dashsoft/x' href='http://dk.dashsoft/x' href='mpxapp://dk.dashsoft/x'>11111111111111</a>"
					+ "<a href='http://mpxapp.dashsoft.dk/x'>22222222222222</a>"
					+ "<a href='http://mpxapp.dashsoft.dk'>33333333333333</a>"
					+ "</body></html>")
			.withSender(new NameAndEmail("Mig", "mig@her.dk"))
			.withRecipient(new NameAndEmail("Dig", "jan@jasys.dk"))
			.send();
		
		System.out.println("Successfully sent to: " + result.getGoodRecipients().get(0).getEmail());
	}

}
