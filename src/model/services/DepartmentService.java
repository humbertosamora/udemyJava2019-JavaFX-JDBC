package model.services;

import java.util.List;

import db.DB;
import model.dao.DepartmentDAO;
import model.dao.jdbc.DepartmentDAOJDBC;
import model.entities.Department;

public class DepartmentService {
	
	private DepartmentDAO dao = new DepartmentDAOJDBC(DB.getConnection());
	
	public List<Department> findAll(){
		return dao.findAll();
	}
	
	public void saveOrupdate(Department obj) {
		// Save new
		if (obj.getId() == null) {
			dao.insert(obj);
		}
		// Update
		else {
			dao.update(obj);
		}
	}
	
	public void remove(Department obj) {
		dao.delete(obj);
	}
	
}
