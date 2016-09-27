import java.sql.Connection;
import java.sql.DriverManager;

public class Hotel {

	public static void main(String[] args) {
		

	}
	//creates connection with database
		public static Connection getConnection() {
			try {
				String user = "root";
				String pass = "perlbak";
				String url = "jdbc:mysql://localhost:3306/hotel?autoReconnect=true&useSSL=false";
				Connection conn = DriverManager.getConnection(url, user, pass);
				return conn;
			} catch (Exception e) {
				System.out.println(e);
			}
			return null;
		}
}
