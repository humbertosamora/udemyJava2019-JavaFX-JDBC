package db;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DB {
	
	private static Connection conn = null;
	
	public static Connection getConnection() {
		return conn;
	}
	
	public static Connection getConnection(String propertiesPath) {
		
		Properties props = loadProperties(propertiesPath);
		
		String url = props.getProperty("dburl");
		
		try {
			conn = DriverManager.getConnection(url,props);
		}
		catch (SQLException e) {
			throw new DBException(e.getMessage());
		}
		
		return conn;
	}
	
	public static void closeConnection() {
		
		if (conn != null) {
			
			try {
				conn.close();
			}
			catch (SQLException e) {
				throw new DBException(e.getMessage());
			}
			
			conn = null;
		}
	}
	
	private static Properties loadProperties(String propertiesPath) {
		
		Properties props = new Properties();
		
		try (FileInputStream in = new FileInputStream(propertiesPath)) { 
			props.load(in);
		}
		catch (FileNotFoundException e) {
			throw new DBException(e.getMessage());
		}
		catch (IOException e) {
			throw new DBException(e.getMessage());
		}
		
		return props;
	}
	
	public static void closeStatement(Statement st) {

		try {
			if (st != null && !st.isClosed()) {
				st.close();
			}
		} catch (SQLException e) {
			System.out.println("DB closeStatement Error: " + e.getMessage());
		}
	}

	public static void closeResultSet(ResultSet rs) {

		try {
			if (rs != null && !rs.isClosed()) {
				rs.close();
			}
		} catch (SQLException e) {
			System.out.println("DB closeStatement Error: " + e.getMessage());
		}
	}

	
}
