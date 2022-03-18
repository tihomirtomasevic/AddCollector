package hr.tt.addcrawler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class PageGeter {
	
	public static final boolean useProxy = Boolean.getBoolean(PropertiesCache.getInstance().getProperty("useProxy"));
	
	public static final String proxyUrl = PropertiesCache.getInstance().getProperty("proxyUrl");
	
	private static final int scheduleSeconds = Integer.parseInt(PropertiesCache.getInstance().getProperty("scheduleSeconds"));

	public static void main(String[] args) {
		try {
			while (true) {
				collectAdds();
				System.out.println("############# PRIKUPLJANJE GOTOVO ###############");
				Thread.sleep(scheduleSeconds * 1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void collectAdds() throws MalformedURLException, IOException {
		boolean shouldSendMail = false;
		Integer numberOfPages = 0;
		StringBuilder stringBuilder = new StringBuilder();
		
		Integer totalCount = 0;
		
		List<Add> adds = new ArrayList<Add>();
		String urlNjuskalo = PropertiesCache.getInstance().getProperty("urlNjuskalo");
		numberOfPages = NjuskaloParser.getNumberOfPages(urlNjuskalo);
		if (numberOfPages > 0) {
			for (int i = 1; i <= numberOfPages; i++) {
				if (i == 1) {
					adds.addAll(NjuskaloParser.extractContentfromURl(urlNjuskalo));
				} else {
					adds.addAll(NjuskaloParser.extractContentfromURl(urlNjuskalo + "?page=" + i));
				}
				
			}
		}
		totalCount += adds.size();
		
		if (adds != null && adds.size() > 0) {
			shouldSendMail = true;
			MailSender.constructMailBody(adds, stringBuilder, "___________Njuskalo OGLASI___________________________________________");
		}
		
		String urlIndex = PropertiesCache.getInstance().getProperty("urlIndex");
		
		adds = new ArrayList<Add>();
		numberOfPages = IndexParser.getNumberOfPages(urlIndex);
		
		if (numberOfPages > 0) {
			for (int i = 1; i <= numberOfPages; i++) {
				adds.addAll(IndexParser.extractContentfromURl(urlIndex + "&num=" + i));
			}
		}
		totalCount += adds.size();
		
		if (adds != null && adds.size() > 0) {
			shouldSendMail = true;
			MailSender.constructMailBody(adds, stringBuilder, "___________Index OGLASI___________________________________________");
		}
		
		System.out.println("######################### SAKUPLJENIH OGLASA:" + totalCount + " #############################");
		if (shouldSendMail) {
			MailSender.sendMail(stringBuilder.toString());
		}
		System.out.println("############# GOTOVO #############");
	}
}
