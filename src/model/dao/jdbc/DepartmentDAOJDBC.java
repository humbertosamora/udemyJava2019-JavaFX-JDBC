package model.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DBException;
import model.dao.DepartmentDAO;
import model.entities.Department;

public class DepartmentDAOJDBC implements DepartmentDAO {
	
	Connection connection;
	
	public DepartmentDAOJDBC(Connection connection) {
		if (connection == null) {
			throw new DBException("DepartmentDAOJDBC error! Invalid parameter!");
		}
		this.connection = connection;
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public void setConnection(Connection connection) {
		if (connection == null) {
			throw new DBException("DepartmentDAOJDBC setConnection error! Invalid parameter!");
		}
		
		this.connection = connection;
	}
	
	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		
		Department department = new Department();
		
		department.setId(rs.getInt("ID"));
		department.setName(rs.getString("NAME"));
		
		return department;
	}
	
	private boolean checkDepartmentData(Department department) {
		if (department==null || department.getName()==null || department.getName().length()==0) {
			return false;
		}
		return true;
	}
	
	@Override
	public Department findById(Integer id) {
		
		PreparedStatement st = null;
		ResultSet rs = null;
		Department dp = null;
		
		try {
			st = connection.prepareStatement("SELECT * FROM DEPARTMENT WHERE ID = ?");

			st.setInt(1, id);
			rs = st.executeQuery();
			
			if (rs.next()) {
				dp = instantiateDepartment(rs);
			}
		}
		catch (SQLException e) {
			throw new DBException("DepartmentDAOJDBC findById error: " + e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
		
		return dp;
		
	}

	@Override
	public Department findByName(String name) {
		
		PreparedStatement st = null;
		ResultSet rs = null;
		Department dp = null;
		
		try {
			st = connection.prepareStatement("SELECT * FROM DEPARTMENT WHERE NAME = BINARY ?");
			
			st.setString(1, name);
			rs = st.executeQuery();
			
			if (rs.next()) {
				dp = instantiateDepartment(rs);
			}
		}
		catch (SQLException e) {
			throw new DBException("DepartmentDAOJDBC findByName error: " + e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
		
		return dp;
	}

	@Override
	public int insert(Department department) {
		
		if (!checkDepartmentData(department)) {
			return 0;
		}
		
		PreparedStatement st = null;
		ResultSet rs = null;
		int rowsAffected = 0;
		
		try {
			Department dp = findByName(department.getName());
			
			if (dp==null || !dp.equals(department)) {
				
				st = connection.prepareStatement(
							"INSERT INTO DEPARTMENT (NAME) VALUES (?)",
							PreparedStatement.RETURN_GENERATED_KEYS);
				
				st.setString(1, department.getName());
				rowsAffected = st.executeUpdate();
				
				rs = st.getGeneratedKeys();
				
				if (rowsAffected > 0 && rs.next()) {
					// getGeneratedKeys() return a ResultSet containing a single column table called GENERATED_KEY
					//System.out.println("Done! Created ID = " + rs.getInt("GENERATED_KEY"));
					department.setId(rs.getInt(1));
				}
			}
			else {
				department.setId(dp.getId());
			}
		}
		catch(SQLException e) {
			throw new DBException("DepartmentDAOJDBC insert error: " + e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
		
		return rowsAffected;
	}

	@Override
	public int update(Department department) {
		
		if (!checkDepartmentData(department)) {
			return 0;
		}
		
		int rowsAffected = 0;
		Department dp = findById(department.getId());
		
		if (dp!=null && !dp.equals(department)) {
			
			PreparedStatement st = null;
			
			try {
				st = connection.prepareStatement("UPDATE DEPARTMENT SET NAME = ? WHERE ID = ?");
				
				st.setString(1, department.getName());
				st.setInt(2, department.getId());
				
				rowsAffected = st.executeUpdate();
			}
			catch(SQLException e) {
				throw new DBException("DepartmentDAOJDBC update error: " + e.getMessage());
			}
			finally {
				DB.closeStatement(st);
			}
			
		}
		
		return rowsAffected;
	}

	@Override
	public int delete(Department department) {
		
		if (!checkDepartmentData(department)) {
			return 0;
		}
		
		int rowsAffected = 0;
		Department dp = findByName(department.getName());
		
		if (dp!=null && dp.equals(department)) {
			
			department.setId(dp.getId());
			
			PreparedStatement st = null;
			
			try {
				st = connection.prepareStatement("DELETE FROM DEPARTMENT WHERE ID = ?");
				st.setInt(1, department.getId());
				rowsAffected = st.executeUpdate();
			}
			catch(SQLException e) {
				throw new DBException("DepartmentDAOJDBC delete error: " + e.getMessage());
			}
			finally {
				DB.closeStatement(st);
			}
		}
		
		return rowsAffected;
	}

	@Override
	public List<Department> findAll() {
		
		PreparedStatement st = null;
		ResultSet rs = null;
		List<Department> departments = null;
		
		try {
			st = connection.prepareStatement("SELECT * FROM DEPARTMENT ORDER BY ID");
			rs = st.executeQuery();
			
			if(rs.isBeforeFirst()) {
				departments = new ArrayList<>();
			
				while (rs.next()) {
					departments.add(instantiateDepartment(rs));
				}
			}
		}
		catch (SQLException e) {
			throw new DBException("DepartmentDAOJDBC findByAll error: " + e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
		
		return departments;
	}

}
