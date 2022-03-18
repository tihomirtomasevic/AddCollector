package hr.tt.addcrawler;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;

import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.config.ServerConfig;
import org.simplejavamail.mailer.config.TransportStrategy;

public class MailSender {
	
	private static final String mailSenderGmailName = "Oglasnik Parser"; // posiljatelj naziv
	// ako se koristi gmail onda treba i APP password -->  https://support.google.com/mail/?p=InvalidSecondFactor
	private static final String mailSenderGmailUsername = PropertiesCache.getInstance().getProperty("mailSenderGmailUsername"); //gmail username preko kojeg saljemo mail 
	
	private static final String mailSenderGmailPassword = PropertiesCache.getInstance().getProperty("mailSenderGmailPassword"); // APP password kojeg generiramo na sigurnosnim postavkama za gmail
	
	private static final String[] recivers = PropertiesCache.getInstance().getProperty("mailRecivers").split(","); // mail adrese na koje se salju mailovi
	
	public static void sendMail(String htmlContent) {
		Mailer mailer = new Mailer(
		        new ServerConfig("smtp.gmail.com", 465, mailSenderGmailUsername, mailSenderGmailPassword),
		        TransportStrategy.SMTP_SSL/*,
		        new ProxyConfig("s-proxy.ericsson.se", 8080)*/
		);
		Properties properties = new Properties();
		properties.setProperty("mail.smtp.timeout", 30 * 1000 + "");
		properties.setProperty("mail.smtp.connectiontimeout", 10 * 1000 + "");
		mailer.applyProperties(properties);

		for (String reciver : recivers) {
			mailer.sendMail(new EmailBuilder()
			        .from(mailSenderGmailName, mailSenderGmailUsername)
			        .to("", reciver)
			        .subject("Novi oglasi")
			        .textHTML(htmlContent)
			        .build());
			System.out.println("Poruka poslana ... " + reciver);
		}
	}
	
	public static String constructMailBody(List<Add> adds, StringBuilder stringBuilder, String siteHeading) {
		stringBuilder.append("<h2>" + siteHeading + "</h2>");
		stringBuilder.append("<ul>");
		for (Add add : adds) {
			constructSingleAddHtml(add, stringBuilder);
		}
		stringBuilder.append("</ul>");
		return stringBuilder.toString();
	}
	
	private static String constructSingleAddHtml(Add add, StringBuilder stringBuilder) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		stringBuilder.append("<li>");
		stringBuilder.append("<table>");
		stringBuilder.append("<tr>");
		stringBuilder.append("<td>");
		stringBuilder.append("<img src=\"" + add.getSlikaUrl() +  "\">");
		stringBuilder.append("</img>");
		stringBuilder.append("</td>");
		stringBuilder.append("<td>");
		stringBuilder.append("<h3>");
		stringBuilder.append("<a href=\"" + add.getUrl() + "\">");
		stringBuilder.append(add.getNaslov());
		stringBuilder.append("</a>");
		stringBuilder.append("</h3>");
		stringBuilder.append("<br>");
		stringBuilder.append(add.getOpis());
		stringBuilder.append("<br>");
		stringBuilder.append("Cijena EUR: <b>" + add.getCijenaEuro() + "</b>  Cijena KN: " + add.getCijenaKn());
		stringBuilder.append("<br>");
		stringBuilder.append("Datum objave: " + sdf.format(add.getDatumObjave()));
		stringBuilder.append("</td>");
		stringBuilder.append("</tr>");
		stringBuilder.append("</table>");
		stringBuilder.append("</li>");
		return stringBuilder.toString();
	}
}
