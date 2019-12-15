package model.services;

import java.util.List;

import db.DB;
import model.dao.SellerDAO;
import model.dao.jdbc.SellerDAOJDBC;
import model.entities.Seller;

public class SellerService {
	
	private SellerDAO dao = new SellerDAOJDBC(DB.getConnection());
	
	public List<Seller> findAll(){
		return dao.findAll();
	}
	
	public void saveOrupdate(Seller obj) {
		// Save new
		if (obj.getId() == null) {
			dao.insert(obj);
		}
		// Update
		else {
			dao.update(obj);
		}
	}
	
	public void remove(Seller obj) {
		dao.delete(obj);
	}
	
}
