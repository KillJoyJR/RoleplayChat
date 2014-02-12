package com.gmail.bkunkcu.roleplaychat.Nickname;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.gmail.bkunkcu.roleplaychat.RoleplayChat;

public class DatabaseManager {
	
	private RoleplayChat plugin;
	private Connection connection;
	private Statement statement;
	private ResultSet resultset;
	
	public DatabaseManager(RoleplayChat plugin) {
		this.plugin = plugin;
	}
	
	public void open() {
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().toPath().toString() + "/nicknames.db");	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		createTable();
	}	
	
	public void close() {
		if(connection != null) {
			try {
                connection.close();
            } catch (SQLException e) {}
		}
	}
	
	private boolean isOpen() {
		if(connection != null)
			return true;
		else
			return false;
		
	}
	
	private void createTable() {
		query("CREATE TABLE IF NOT EXISTS nicknames (username VARCHAR(20), nickname VARCHAR(20))");
	}
	
	
	public ResultSet query(String query) {
		if(isOpen()) {
			try {
				statement = connection.createStatement();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			if(query.startsWith("SELECT")) {
				try {
					resultset = statement.executeQuery(query);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				try {
					statement.executeUpdate(query);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
						
			return resultset;
		}
		
		return null;
	}
}