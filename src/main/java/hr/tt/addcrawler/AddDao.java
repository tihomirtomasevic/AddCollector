package hr.tt.addcrawler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class AddDao {
	
	private static final String dbUser = "admin";
	
	private static final String dbPassword = "admin";
	
	private static final String dbLocation = "./h2db/h2db";
	
	public static Add checkIfAddExists(Integer sifra, Integer site) throws SQLException {

		Connection dbConnection = null;
		Statement statement = null;
		String selectTableSQL = "SELECT ID, SIFRA, NASLOV, OPIS, CIJENA_KN, CIJENA_EURO, URL, SLIKA_URL, DATUM_OBJAVE, DATUM FROM ADDS.OGLASI WHERE SIFRA = " + sifra + " AND SITE = " + site;
		try {
			dbConnection = getDBConnection();
			statement = dbConnection.createStatement();
			System.out.println(selectTableSQL);
			// execute select SQL stetement
			ResultSet rs = statement.executeQuery(selectTableSQL);
			while (rs.next()) {
				Add add = new Add();
				add.setId(rs.getInt("ID"));
				add.setSifra(rs.getString("SIFRA"));
				add.setNaslov(rs.getString("NASLOV"));
				add.setOpis(rs.getString("OPIS"));
				add.setCijenaKn(rs.getInt("CIJENA_KN"));
				add.setCijenaEuro(rs.getInt("CIJENA_EURO"));
				add.setUrl(rs.getString("URL"));
				add.setSlikaUrl(rs.getString("SLIKA_URL"));
				add.setDatumObjave(rs.getDate("DATUM_OBJAVE"));
				add.setDatum(rs.getDate("DATUM"));
				return add;
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			if (statement != null) {
				statement.close();
			}
			if (dbConnection != null) {
				dbConnection.close();
			}
		}
		return null;
	}
	
	public static void insertAddInDatabase(String sifra, String naslov, String opis, Integer cijenaKn, Integer cijenaEuro, String url, String slikaUrl, Date datumObjave, Integer site) throws SQLException {

		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;

		String insertTableSQL = "INSERT INTO ADDS.OGLASI "
				+ "( ID, SIFRA, NASLOV, CIJENA_KN, CIJENA_EURO, URL, SLIKA_URL, DATUM_OBJAVE, DATUM, OPIS, SITE ) VALUES " 
				+ "( ?    , ?     , ?        , ?          , ?   , ?       , ?           , ?    , ?  , ?, ?  )";
		try {
			dbConnection = getDBConnection();
			preparedStatement = dbConnection.prepareStatement(insertTableSQL);

			Long id = getSequence();
			preparedStatement.setLong(1, id);
			preparedStatement.setString(2, sifra);
			preparedStatement.setString(3, naslov);
			preparedStatement.setInt(4, cijenaKn);
			preparedStatement.setInt(5, cijenaEuro);
			preparedStatement.setString(6, url);
			preparedStatement.setString(7, slikaUrl);
			preparedStatement.setTimestamp(8, new java.sql.Timestamp(datumObjave.getTime()));
			preparedStatement.setTimestamp(9, new java.sql.Timestamp(new Date().getTime()));
			preparedStatement.setString(10, opis);
			preparedStatement.setInt(11, site);

			// execute insert SQL stetement
			preparedStatement.executeUpdate();

			System.out.println("INSERTAN REDAK, SIFRA = " + sifra);
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			if (preparedStatement != null) {
				preparedStatement.close();
			}
			if (dbConnection != null) {
				dbConnection.close();
			}
		}
	}
	
	public static void updateAddInDatabase(Integer id, String sifra, String naslov, String opis, Integer cijenaKn, Integer cijenaEuro, String url, String slikaUrl, Date datumObjave) throws SQLException {

		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;

		String insertTableSQL = "UPDATE ADDS.OGLASI SET "
				+ " SIFRA = ?, NASLOV = ?, CIJENA_KN = ?, CIJENA_EURO = ?, URL = ?, SLIKA_URL = ?, DATUM_OBJAVE = ?, DATUM = ?, OPIS = ? " 
				+ " WHERE ID = ? ";
		try {
			dbConnection = getDBConnection();
			preparedStatement = dbConnection.prepareStatement(insertTableSQL);

			preparedStatement.setString(1, sifra);
			preparedStatement.setString(2, naslov);
			preparedStatement.setInt(3, cijenaKn);
			preparedStatement.setInt(4, cijenaEuro);
			preparedStatement.setString(5, url);
			preparedStatement.setString(6, slikaUrl);
			preparedStatement.setTimestamp(7, new java.sql.Timestamp(datumObjave.getTime()));
			preparedStatement.setTimestamp(8, new java.sql.Timestamp(new Date().getTime()));
			preparedStatement.setString(9, opis);
			preparedStatement.setLong(10, id);

			// execute insert SQL stetement
			preparedStatement.executeUpdate();

			System.out.println("UPDATEAN REDAK, ID = " + id);
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			if (preparedStatement != null) {
				preparedStatement.close();
			}
			if (dbConnection != null) {
				dbConnection.close();
			}
		}
	}
	
	private static Long getSequence() throws SQLException {
		String sqlIdentifier = "select ADDS.OGLASI_SEQ.NEXTVAL from dual";
		PreparedStatement pst = getDBConnection().prepareStatement(sqlIdentifier);
		ResultSet rs = pst.executeQuery();
		Long id = null;
		if(rs.next()) {
		     id = rs.getLong(1);
		}
		return id;
	}

	private static Connection getDBConnection() {
		Connection dbConnection = null;
		try {
			Class.forName("org.h2.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
		try {
			dbConnection = DriverManager.
		            getConnection("jdbc:h2:" + dbLocation, dbUser, dbPassword);
			return dbConnection;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return dbConnection;
	}
}
