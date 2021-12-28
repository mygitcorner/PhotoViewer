package gf.photoviewer.io;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import gf.photoviewer.resources.LabeledIdentity;

public class LabeledIndentityIO<E extends LabeledIdentity> {
	protected Connection conn;
	
	private String tableName;
	private Class<E> classObj;
	
	protected LabeledIndentityIO(String tableName, Class<E> classObj) throws Exception {
		conn = DatabaseManager.getinstance().getConnection();
		this.tableName = tableName;
		this.classObj = classObj;
	}
	
	protected E fromResultSet(ResultSet result) throws Exception {
		int id = result.getInt("Id");
		String label = result.getString("Label");
		return classObj.getConstructor(int.class, String.class).newInstance(id, label);
	}
	
	public E add(E element) throws Exception {
		String query = "INSERT INTO " + tableName + " (Label) VALUES (?);"
				+ "SELECT SCOPE_IDENTITY()";
		
		try (PreparedStatement stat = conn.prepareStatement(query)) {
			stat.setString(1, element.getLabel());
			stat.execute();
		}
		
		query = "SELECT * FROM " + tableName + " "
				+ "WHERE Id = ("
				+ "SELECT MAX(Id) FROM " + tableName + ")";
		try (PreparedStatement stat = conn.prepareStatement(query)) {
			ResultSet result = stat.executeQuery();
			result.next();
			return fromResultSet(result);
		}
	}
	
	public E get(int id) throws Exception {
		String query = "SELECT * FROM " + tableName + " WHERE id = ?";
		
		try (PreparedStatement stat = conn.prepareStatement(query)) {
			stat.setInt(1, id);
			ResultSet result = stat.executeQuery();
			
			if (result.next())
				return fromResultSet(result);
			else
				return null;
		}
	}
	
	public List<E> getAllRecords() throws Exception {
		String query = "SELECT * FROM " + tableName;
		
		List<E> elements = new ArrayList<>();
		
		try (Statement stat = conn.createStatement()) {
			ResultSet result = stat.executeQuery(query);
			
			while (result.next())
				elements.add(fromResultSet(result));
		}
		
		return elements;
	}
	
	public void remove(E element) throws Exception {
		String query = "DELETE FROM " + tableName + " WHERE Id = ?";
		
		try (PreparedStatement stat = conn.prepareStatement(query)) {
			stat.setInt(1, element.getId());
			stat.executeUpdate();
		}
	}
	
	public void rename(E element, String newName) throws Exception {
		String query = "UPDATE " + tableName + " SET Label = ? WHERE Id = ?";
		
		try (PreparedStatement stat = conn.prepareStatement(query)) {
			stat.setString(1, newName);
			stat.setInt(2, element.getId());
			stat.executeUpdate();
		}
	}
}
