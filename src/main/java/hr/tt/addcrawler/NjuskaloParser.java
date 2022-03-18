package hr.tt.addcrawler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class NjuskaloParser {
	public static Integer getNumberOfPages(String firstUrl) throws MalformedURLException, IOException {
		Document doc = Util.getDOMDocumentFromUrlNjuskalo(firstUrl);
		Double count = Double.parseDouble(doc.getElementsByClass("entities-count").first().text());
		Integer numberOfPages = (int) Math.ceil((count) / 25.0);
		return numberOfPages;
	}
	
	public static List<Add> extractContentfromURl(String url) {
		List<Add> addsForSend = new ArrayList<Add>();
		try {
			Document doc = Util.getDOMDocumentFromUrlNjuskalo(url);
			Elements elements = doc.getElementsByClass("EntityList-items");

			if (elements.size() == 4) {
				// drugi i treci su oglasi na njuskalu - prvi i cetvrti su reklame
				Element list = elements.get(1); //vau-vau
				addsForSend.addAll(extractAdds(list));
				list = elements.get(2); //obicni oglasi
				addsForSend.addAll(extractAdds(list));
			} else if (elements.size() == 3) {
				// nema vau-vau oglasa
				Element list = elements.get(1); //obicni oglasi
				addsForSend.addAll(extractAdds(list));
			} else {
				System.out.println("##################################### NEPOZNATA STRANICA ###################################");
			}
		} catch ( Exception ex ) {
			ex.printStackTrace();
		}
		return addsForSend;
	}
	
	private static List<Add> extractAdds(Element list) throws NumberFormatException, SQLException, ParseException {
		Elements articles = list.getElementsByTag("article");
		List<Add> adds = new ArrayList<Add>();
		for (Element article : articles) {
			//micanje nepotrebnih elemenata
			article.getElementsByClass("entity-tools").remove();
			article.getElementsByClass("entity-thumbnail-preloader").remove();
			article.getElementsByTag("noscript").remove();
			article.getElementsByTag("entity-notice entity-notice--compare-ad notice-ad-compare js-comparisonNotice  hidden  ").remove();
			
			//dohvati sifru
			String sifra = article.getElementsByClass("entity-title").first().getElementsByTag("a").first().attr("name");
			System.out.println("Sifra:" + sifra);
			
			//dohvati naslov
			String naslov = article.getElementsByClass("entity-title").first().getElementsByTag("a").first().text();
			System.out.println("Naslov:" + naslov);
			
			String opis = article.getElementsByClass("entity-description-main").first().text();
			System.out.println("Opis:" + opis);
			
			//dohvati url
			Element urlEl = article.getElementsByClass("entity-title").first().getElementsByTag("a").first();
			String url = "http://www.njuskalo.hr" + urlEl.attr("href");
			System.out.println("URL:" + url);
			
			//dohvati url_slike
			urlEl = article.getElementsByTag("img").first();
			String slikaUrl = "http:" + urlEl.attr("data-src"); 
			System.out.println("Slika URL:" + slikaUrl);
			
			
			String tmp = article.getElementsByClass("price price--hrk").first().text().replaceAll("\\.","").replaceAll("kn", "");
			tmp = tmp.substring(0, tmp.length() - 1);
			Integer cijenaKn = Integer.parseInt(tmp);
			System.out.println("cijenaKn:" + cijenaKn);
			
			tmp = article.getElementsByClass("price price--eur").first().text().replaceAll("\\.","").replaceAll("€ ~", "");
			tmp = tmp.substring(0, tmp.length() - 1);
			Integer cijenaEuro = Integer.parseInt(tmp);
			System.out.println("cijenaEuro:" + cijenaEuro);
			
			String datumObj = article.getElementsByTag("time").first().attr("datetime");
			Date datumObjave = Util.parseDateTime(datumObj);
			System.out.println("datum objave:" + datumObj);
			
			Add add = AddDao.checkIfAddExists(Integer.parseInt(sifra), Add.NJUSKALO_SITE);
			
			if (add == null) {
				System.out.println("************** NOVI OGLAS, INSERT U DB ******************");
				// insert u bazu
				AddDao.insertAddInDatabase(sifra, naslov, opis, cijenaKn, cijenaEuro, url, slikaUrl, datumObjave, Add.NJUSKALO_SITE);
				// stavi ga u listu za mail
				add = new Add(sifra, naslov, opis, cijenaKn, cijenaEuro, url, slikaUrl, datumObjave);
				adds.add(add);
			} else {
				System.out.println("************** POSTOJECI OGLAS ******************");
				// provjeri je li se nesto promijenilo
				boolean shouldUpdate = false;
				if (!add.getNaslov().equals(naslov)) {
					// promijenjen naslov
					add.setOpis(add.getOpis() + "<br><b style=\"color:red\">Promijenjen naslov, prije:" + add.getNaslov()  + "</b>");
					add.setNaslov(naslov);
					shouldUpdate = true;
				}
				if (!add.getOpis().equals(opis)) {
					// promijenjen opis
					add.setOpis(opis + "<br><b style=\"color:red\">Promijenjen opis, prije:" + add.getOpis() + "</b>");
					shouldUpdate = true;
				}
				if (add.getCijenaEuro().intValue() != cijenaEuro.intValue()) {
					// promijenjena cijena euro
					add.setOpis(add.getOpis() + "<br><b style=\"color:red\">Promijenjen cijena EURO, prije:" + add.getCijenaEuro() + "</b>");
					add.setCijenaEuro(cijenaEuro);
					shouldUpdate = true;
//				} else if (add.getCijenaKn().intValue() != cijenaKn.intValue()) {
//					// promijenjena cijena kn
//					add.setOpis(add.getOpis() + "<br><b style=\"color:red\">Promijenjen cijena KN, prije:" + add.getCijenaKn() + "</b>");
//					add.setCijenaKn(cijenaKn);
//					shouldUpdate = true;
				}
				if (shouldUpdate) {
					// napravi update u bazi s novim podacima
					AddDao.updateAddInDatabase(add.getId(), sifra, naslov, opis, cijenaKn, cijenaEuro, url, slikaUrl, datumObjave);
					System.out.println("******************** OGLAS SE PROMIJENIO - UPDATE *****************************");
					adds.add(add);
				}
			}
		}
		return adds;
	}
}
