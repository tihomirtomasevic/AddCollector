package hr.tt.addcrawler;

import java.util.Date;

public class Add {
	
	private Integer id;

	private String sifra;
	
	private String naslov;
	
	private String opis;
	
	private Integer cijenaKn;
	
	private Integer cijenaEuro;
	
	private String url;
	
	private String slikaUrl;
	
	private Date datumObjave;
	
	private Date datum;
	
	public static final int NJUSKALO_SITE = 0;
	
	public static final int INDEX_SITE = 1;
	
	public Add() {
	}

	public Add(String sifra, String naslov, String opis, Integer cijenaKn, Integer cijenaEuro, String url,
			String slikaUrl, Date datumObjave) {
		this.sifra = sifra;
		this.naslov = naslov;
		this.opis = opis;
		this.cijenaKn = cijenaKn;
		this.cijenaEuro = cijenaEuro;
		this.url = url;
		this.slikaUrl = slikaUrl;
		this.datumObjave = datumObjave;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSifra() {
		return sifra;
	}

	public void setSifra(String sifra) {
		this.sifra = sifra;
	}

	public String getNaslov() {
		return naslov;
	}

	public void setNaslov(String naslov) {
		this.naslov = naslov;
	}

	public String getOpis() {
		return opis;
	}

	public void setOpis(String opis) {
		this.opis = opis;
	}

	public Integer getCijenaKn() {
		return cijenaKn;
	}

	public void setCijenaKn(Integer cijenaKn) {
		this.cijenaKn = cijenaKn;
	}

	public Integer getCijenaEuro() {
		return cijenaEuro;
	}

	public void setCijenaEuro(Integer cijenaEuro) {
		this.cijenaEuro = cijenaEuro;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSlikaUrl() {
		return slikaUrl;
	}

	public void setSlikaUrl(String slikaUrl) {
		this.slikaUrl = slikaUrl;
	}

	public Date getDatumObjave() {
		return datumObjave;
	}

	public void setDatumObjave(Date datumObjave) {
		this.datumObjave = datumObjave;
	}

	public Date getDatum() {
		return datum;
	}

	public void setDatum(Date datum) {
		this.datum = datum;
	}
	
	
}
