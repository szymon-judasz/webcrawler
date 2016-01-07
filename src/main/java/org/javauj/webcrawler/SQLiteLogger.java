package org.javauj.webcrawler;

import java.io.Closeable;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Observable;
import java.util.Observer;

public class SQLiteLogger implements Closeable, Observer {
	private static final String tablename = "visitedpages";
	private static final String tableDefinition = "(id integer primary key, url string, timestamp DATE DEFAULT (datetime('now','localtime')))";
	private String dbName;
	private Connection dbConnection;
	private boolean connectionEstablished;

	public SQLiteLogger(String databaseName, boolean purgeOldDb) {
		dbName = databaseName;
		connectionEstablished = false;
		try {
			Class.forName("org.sqlite.JDBC");
			
			dbConnection = DriverManager.getConnection("jdbc:sqlite:" + dbName);
			Statement statement = dbConnection.createStatement();
			statement.setQueryTimeout(30);
			if(purgeOldDb){
				statement.executeUpdate("Drop table if exists " + tablename);
				statement.executeUpdate("Create table " + tablename + tableDefinition);
			}
			
			connectionEstablished = true;
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean logSite(URL url)
	{
		Statement statement;
		try {
			statement = dbConnection.createStatement();
			statement.setQueryTimeout(1);
			String queryString  = new String("insert into " + tablename + "(url) VALUES ('" + url.toString() + "')");
			statement.executeUpdate(queryString);
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
		
	}
	
	
	@Override
	public void close() {
		if(connectionEstablished == false) {
			return;
		}
		try {
			dbConnection.close();
			connectionEstablished = false;
		} catch (SQLException e){
			e.printStackTrace();
		}
	}
	
	public boolean getConnectionStatus() {
		return connectionEstablished;
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg == null)
		{
			System.err.println("SQLITE: Update called with arg equals to null."); // only for debug purpose
		}
		logSite((URL) arg);
		
	}
}
