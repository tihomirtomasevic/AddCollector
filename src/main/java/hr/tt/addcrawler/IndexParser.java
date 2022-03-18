package hr.tt.addcrawler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class IndexParser {
	public static Integer getNumberOfPages(String firstUrl) throws MalformedURLException, IOException {
		Document doc = Util.getDOMDocumentFromUrl(firstUrl);
		Double count = Double.parseDouble(doc.getElementsByClass("rezultati_header").first().getElementsByTag("strong").first().text());
		Integer numberOfPages = (int) Math.ceil((count) / 100.0);
		return numberOfPages;
	}
	
	public static List<Add> extractContentfromURl(String url) {
		List<Add> addsForSend = new ArrayList<Add>();
		try {
			Document doc = Util.getDOMDocumentFromUrl(url);
			Elements elements = doc.getElementsByClass("results");

			if (elements.size() == 1) {
				Element list = elements.first(); 
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
		Elements articles = list.getElementsByClass("OglasiRezHolder");
		List<Add> adds = new ArrayList<Add>();
		for (Element article : articles) {
			//micanje nepotrebnih elemenata
			
			//provjera radi li se o reklami
			if (!article.getElementsByTag("a").isEmpty()) {
				String url = article.getElementsByTag("a").first().attr("href");
				System.out.println("URL:" + url);

				//dohvati sifru
				String[] splits = url.split("oid/");
				String sifra = splits[1];
				System.out.println("Šifra:" + sifra);

				//dohvati naslov
				String naslov = article.getElementsByClass("title").first().text();
				System.out.println("Naslov:" + naslov);


				//napravi opis
				String opis = "";
				Elements el = article.getElementsByTag("li");
				for (Element element : el) {
					if (!opis.equals("")) {
						opis += " | ";
					}
					opis += element.text().trim();
				}
				System.out.println("Opis:" + opis);

				//dohvati url_slike
				Element urlEl = article.getElementsByTag("img").first();
				String slikaUrl = urlEl.attr("src"); 
				System.out.println("Slika URL:" + slikaUrl);

				String objeCijene = article.getElementsByClass("price").first().getElementsByTag("span").first().text().replaceAll("\\.","").replaceAll(" €", "").replaceAll(" kn", "");
				splits = objeCijene.split(" ~ ");
				Integer cijenaEuro = Integer.parseInt(splits[0]);
				System.out.println("cijenaEuro:" + cijenaEuro);


				Integer cijenaKn = Integer.parseInt(splits[1]);
				System.out.println("cijenaKn:" + cijenaKn);

				String datumObj = article.getElementsByClass("icon-time").first().text();
				Date datumObjave = new Date();
				if (datumObj.contains("Objava ")) {
					// moze se isparsati samo datum
					SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
					datumObjave = sdf.parse(datumObj.replace("Objava ", ""));
				}
				System.out.println("Datum objave:" + datumObj);

				Add add = AddDao.checkIfAddExists(Integer.parseInt(sifra), Add.INDEX_SITE);

				if (add == null) {
					System.out.println("************** NOVI OGLAS, INSERT U DB ******************");
					// insert u bazu
					AddDao.insertAddInDatabase(sifra, naslov, opis, cijenaKn, cijenaEuro, url, slikaUrl, datumObjave, Add.INDEX_SITE);
					// stavi ga u listu za mail
					add = new Add(sifra, naslov, opis, cijenaKn, cijenaEuro, url, slikaUrl, datumObjave);
					adds.add(add);
				} else {
					System.out.println("************** POSTOJECI OGLAS ******************");
					add.setSlikaUrl(slikaUrl);
					// provjeri je li se nesto promijenilo
					boolean shouldUpdate = false;
					if (!add.getNaslov().equals(naslov)) {
						// promijenjen naslov
						add.setOpis(add.getOpis() + "<br><b style=\"color:red\">Promijenjen naslov, prije:" + add.getNaslov()  + "</b>");
						add.setNaslov(naslov);
						shouldUpdate = true;
					}  
					//				if(!add.getOpis().equals(opis)) {
					//					// promijenjen opis
					//					add.setOpis(opis + "<br><b style=\"color:red\">Promijenjen opis, prije:" + add.getOpis() + "</b>");
					//					shouldUpdate = true;
					if (add.getCijenaEuro().intValue() != cijenaEuro.intValue()) {
						// promijenjena cijena euro
						add.setOpis(add.getOpis() + "<br><b style=\"color:red\">Promijenjen cijena EURO, prije:" + add.getCijenaEuro() + "</b>");
						add.setCijenaEuro(cijenaEuro);
						shouldUpdate = true;
//					} else if (add.getCijenaKn().intValue() != cijenaKn.intValue()) {
//						// promijenjena cijena kn
//						add.setOpis(add.getOpis() + "<br><b style=\"color:red\">Promijenjen cijena KN, prije:" + add.getCijenaKn() + "</b>");
//						add.setCijenaKn(cijenaKn);
//						shouldUpdate = true;
					}
					if (shouldUpdate) {
						// napravi update u bazi s novim podacima
						AddDao.updateAddInDatabase(add.getId(), sifra, naslov, opis, cijenaKn, cijenaEuro, url, slikaUrl, datumObjave);
						System.out.println("******************** OGLAS SE PROMIJENIO - UPDATE *****************************");
						adds.add(add);
					}
				}
			}
		}
		return adds;
	}
}
