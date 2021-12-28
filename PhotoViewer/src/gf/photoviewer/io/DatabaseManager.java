package gf.photoviewer.io;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
	private static final String url = "jdbc:sqlite:PhotoViewer\\resources\\PhotoViewerDBTest.db";
	private static DatabaseManager singleton = null;
	private Connection conn;
	
	public static DatabaseManager getinstance() throws SQLException {
		if (singleton == null)
			singleton = new DatabaseManager();
		
		return singleton;
	}
	
	private DatabaseManager() throws SQLException {
		conn = DriverManager.getConnection(url);
		
		String query;
		try (Statement stat = conn.createStatement()) {
			query = "CREATE TABLE IF NOT EXISTS Pictures ("
					+ "Id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "AlbumId INTEGER,"
					+ "Path TEXT,"
					+ "Label TEXT)";
			stat.executeUpdate(query);
			
			query = "CREATE TABLE IF NOT EXISTS Albums ("
					+ "Id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "Label TEXT)";
			stat.executeUpdate(query);
			
			query = "CREATE TABLE IF NOT EXISTS Tags ("
					+ "Id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "Label TEXT)";
			stat.executeUpdate(query);
			
			query = "CREATE TABLE IF NOT EXISTS TagPicturePairs ("
					+ "TagId INTEGER,"
					+ "PictureId INTEGER)";
			stat.executeUpdate(query);
		}
	}
	
	public Connection getConnection() throws SQLException {
		return conn;
	}
}
