package model.dao;

import java.sql.Connection;

import db.DBException;
import model.dao.jdbc.DepartmentDAOJDBC;
import model.dao.jdbc.SellerDAOJDBC;

public class DAOFactory {
	
	public static SellerDAO createSellerDAO(Connection connection) {
		if (connection == null) {
			throw new DBException("DAOFactory createSellerDAO error! Invalid parameter!");
		}
		return new SellerDAOJDBC(connection);
	}
	
	public static DepartmentDAO createDepartmentDAO(Connection connection) {
		if (connection == null) {
			throw new DBException("DAOFactory createSellerDAO error! Invalid parameter!");
		}
		return new DepartmentDAOJDBC(connection);
	}
}
