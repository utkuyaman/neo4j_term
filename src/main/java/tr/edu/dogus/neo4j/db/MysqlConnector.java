package tr.edu.dogus.neo4j.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author toshiba
 */
public class MysqlConnector {

	public MysqlConnector() {

	}

	public List<TwitterUser> getAllTwitterUser() {

		ArrayList<TwitterUser> ret = new ArrayList<TwitterUser>();

		try {
			try {
				Class.forName("com.mysql.jdbc.Driver").newInstance();
			} catch (ClassNotFoundException ex) {
				Logger.getLogger(MysqlConnector.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (InstantiationException ex) {
			Logger.getLogger(MysqlConnector.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			Logger.getLogger(MysqlConnector.class.getName()).log(Level.SEVERE, null, ex);
		}

		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tweet", "root", "");
		} catch (SQLException ex) {
			Logger.getLogger(MysqlConnector.class.getName()).log(Level.SEVERE, null, ex);
		}

		try {
			Statement st = conn.createStatement();
			ResultSet res = st.executeQuery("SELECT * FROM  twitteruser limit 10");

			while (res.next()) {
				Integer userId = res.getInt("user_id");
				Long twitterUserId = res.getLong("twitter_user_id");
				String name = res.getString("name");
				System.out.println("db:" + userId.toString() + "\t" + twitterUserId.toString() + "\t" + name);

				ret.add(new TwitterUser(userId, twitterUserId, name));
			}

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	public List<TwitterUser> getAllFriends() {
		return null;
	}

	public List<TwitterUser> getAllFollowers() {
		return null;
	}

}
