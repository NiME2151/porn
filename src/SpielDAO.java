import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SpielDAO {
	
	public Spiel select(int id) throws ClassNotFoundException, SQLException {
		Spiel spiel = new Spiel();
		try {
			String sql = "";
			// connect()-Methode wird ausgef�hrt um eine Verbindung zur Datenbank
			// herzustellen
			Connection conn = ConnectToDB.connectToDB();
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(sql);
			rs.next();
			spiel.setId(rs.getInt("id"));
			spiel.setTitel(rs.getString("titel"));
			spiel.setVeroeffentlichkeitsdatum(rs.getString("ver�ffentilichkeitsdatum"));
			spiel.setUsk(rs.getString("usk"));
			spiel.setPreis(rs.getDouble("preis"));
			spiel.setLageranzahl(rs.getInt("lageranzahl"));
			spiel.setVerfuegbarkeit(rs.getString("verf�gbarkeit"));
			spiel.setSprache(rs.getString("sprache"));
			// Gibt Nachricht aus bei funktionierendem SELECT
			System.out.println("SQL-SELECT funzt");
			return spiel;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
}