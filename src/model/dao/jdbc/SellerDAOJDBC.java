package model.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DBException;
import model.dao.SellerDAO;
import model.entities.Department;
import model.entities.Seller;

public class SellerDAOJDBC implements SellerDAO {
	
	Map <Integer, Department> departments = new HashMap<>();
	Connection connection;
	
	public SellerDAOJDBC(Connection connection) {
		
		if (connection==null) {
			throw new DBException("SellerDAOJDBC error! Invalid parameters!");
		}
		
		this.connection = connection;
	}
	
	private Seller instantiateSeller(ResultSet rs) throws SQLException {
		
		Seller seller = new Seller();
		
		seller.setBaseSalary(rs.getDouble("BASESALARY"));
		seller.setBirthdate(new java.util.Date(rs.getTimestamp("BIRTHDATE").getTime()));
		seller.setEmail(rs.getString("EMAIL"));
		seller.setId(rs.getInt("ID"));
		seller.setName(rs.getString("NAME"));
		
		Integer departmentId = rs.getInt("DEPARTMENTID");
		
		if (departments.get(departmentId) == null) {
			departments.put(departmentId, new Department(departmentId, rs.getString("DEPNAME")));
		}
		
		seller.setDepartment(departments.get(departmentId));
		
		return seller;
	}
	
	private boolean checkSellerData(Seller seller) {
		
		if (seller==null || seller.getBaseSalary()==null || seller.getBirthdate()==null
				|| seller.getDepartment()==null || seller.getEmail()==null
				/*|| seller.getId()==null*/ || seller.getName()==null) { // Seller.Id will be automatically generated
			return false;
		}
		
		if (seller.getEmail().length()==0 || seller.getName().length()==0) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public Seller findById(Integer id) {
		
		PreparedStatement st = null;
		ResultSet rs = null;
		Seller seller = null;
		
		try {
			st = connection.prepareStatement("SELECT SELLER.*, DEPARTMENT.NAME AS DEPNAME "
					+ "FROM SELLER INNER JOIN DEPARTMENT "
					+ "ON SELLER.DEPARTMENTID = DEPARTMENT.ID WHERE SELLER.ID = ?");

			st.setInt(1, id);
			rs = st.executeQuery();
			
			if (rs.next()) {
				seller = instantiateSeller(rs);
			}
		}
		catch (SQLException e) {
			throw new DBException("SellerDAOJDBC findById error: " + e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
		
		return seller;
	}

	@Override
	public Seller findByName(String name) {
		
		PreparedStatement st = null;
		ResultSet rs = null;
		Seller seller = null;
		
		try {
			st = connection.prepareStatement("SELECT SELLER.*, DEPARTMENT.NAME AS DEPNAME "
											+ "FROM SELLER INNER JOIN DEPARTMENT " 
											+ "ON SELLER.DEPARTMENTID = DEPARTMENT.ID "
											+ "WHERE SELLER.NAME = BINARY ?");
			
			st.setString(1, name);
			rs = st.executeQuery();
			
			if (rs.next()) {
				seller = instantiateSeller(rs);
			}
		}
		catch (SQLException e) {
			throw new DBException("SellerDAOJDBC findByName error: " + e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
		
		return seller;
	}

	@Override
	public int insert(Seller seller) {
		
		if (!checkSellerData(seller)) {
			return 0;
		}
		
		PreparedStatement st = null;
		ResultSet rs = null;
		int rowsAffected = 0;
		
		try {
			Seller s = findByName(seller.getName());
			
			if (s==null || !seller.equals(s)) {
				
				st = connection.prepareStatement(
							"INSERT INTO SELLER " +
							"(NAME, EMAIL, BIRTHDATE, BASESALARY, DEPARTMENTID) " +
							"VALUES (?, ?, ?, ?, ?)",
							PreparedStatement.RETURN_GENERATED_KEYS);
				
				st.setString(1, seller.getName());
				st.setString(2, seller.getEmail());
				st.setDate(3, new java.sql.Date(seller.getBirthdate().getTime()));
				st.setDouble(4, seller.getBaseSalary());
				st.setInt(5, seller.getDepartment().getId());
				
				rowsAffected = st.executeUpdate();
				
				rs = st.getGeneratedKeys();
	
				if (rowsAffected > 0 && rs.next()) {
					// getGeneratedKeys() return a ResultSet containing a single column table called GENERATED_KEY
					//System.out.println("Done! Created ID = " + rs.getInt("GENERATED_KEY"));
					seller.setId(rs.getInt(1));
				}
			}
			else {
				seller.setId(s.getId());
			}
		}
		catch(SQLException e) {
			throw new DBException("SellerDAOJDBC insert error: " + e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
		
		return rowsAffected;
	}

	@Override
	public int update(Seller seller) {
		
		if (!checkSellerData(seller)) {
			return 0;
		}
		
		int rowsAffected = 0;
		Seller s = findByName(seller.getName());
		
		if (s!=null && s.equals(seller)) {
			
			seller.setId(s.getId());
			
			PreparedStatement st = null;
			
			try {
				st = connection.prepareStatement("UPDATE SELLER " 
												+ "SET NAME = ?, EMAIL = ?, BIRTHDATE = ?, BASESALARY = ?, DEPARTMENTID = ? "
												+ "WHERE ID = ?");
				
				st.setString(1, seller.getName());
				st.setString(2, seller.getEmail());
				st.setDate(3, new java.sql.Date(seller.getBirthdate().getTime()));
				st.setDouble(4, seller.getBaseSalary());
				st.setInt(5, seller.getDepartment().getId());
				st.setInt(6, seller.getId());
				
				rowsAffected = st.executeUpdate();
			}
			catch(SQLException e) {
				throw new DBException("SellerDAOJDBC update error: " + e.getMessage());
			}
			finally {
				DB.closeStatement(st);
			}
			
		}
		
		return rowsAffected;
	}

	@Override
	public int delete(Seller seller) {
		
		if (!checkSellerData(seller)) {
			return 0;
		}
		
		int rowsAffected = 0;
		Seller s = findByName(seller.getName());
		
		if (s!=null && s.equals(seller)) {
			
			seller.setId(s.getId());
			
			PreparedStatement st = null;
			
			try {
				st = connection.prepareStatement("DELETE FROM SELLER WHERE ID = ?");
				st.setInt(1, seller.getId());
				rowsAffected = st.executeUpdate();
			}
			catch(SQLException e) {
				throw new DBException("SellerDAOJDBC delete error: " + e.getMessage());
			}
			finally {
				DB.closeStatement(st);
			}
		}
		
		return rowsAffected;
	}

	@Override
	public List<Seller> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		List<Seller> sellers = null;
		
		try {
			st = connection.prepareStatement("SELECT SELLER.*, DEPARTMENT.NAME AS DEPNAME "
											+ "FROM SELLER INNER JOIN DEPARTMENT "
											+ "ON SELLER.DEPARTMENTID = DEPARTMENT.ID "
											+ "ORDER BY SELLER.ID");
			rs = st.executeQuery();
			
			if(rs.isBeforeFirst()) {
				sellers = new ArrayList<>();
			
				while (rs.next()) {
					sellers.add(instantiateSeller(rs));
				}
			}
		}
		catch (SQLException e) {
			throw new DBException("Seller findByAll error: " + e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
		
		return sellers;
	}

}
