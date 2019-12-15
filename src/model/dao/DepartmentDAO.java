package model.dao;

import java.util.List;

import model.entities.Department;

public interface DepartmentDAO {

	Department findById(Integer id);
	Department findByName(String name);
	int insert(Department department);
	int update(Department department);
	int delete(Department department);
	List<Department> findAll();
	
}
