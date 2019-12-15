package model.dao;

import java.util.List;

import model.entities.Seller;

public interface SellerDAO {
	
	Seller findById(Integer id);
	Seller findByName(String name);
	int insert(Seller seller);
	int update(Seller seller);
	int delete(Seller seller);
	List<Seller> findAll();
	
}
