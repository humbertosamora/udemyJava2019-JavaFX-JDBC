package model.entities;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Objects;

public class Seller {

	private Integer id;
	private String name;
	private String email;
	private Double baseSalary;
	private LocalDate birthdate;
	private Department department;
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); 
	private static DecimalFormat nf = new DecimalFormat("#.##");
	
	public Seller() {
		
	}

	public Seller(Integer id, String name, String email, Double baseSalary, LocalDate birthdate, Department department) {
		this.id = id;
		this.name = name.toUpperCase();
		this.email = email.toUpperCase();
		this.baseSalary = baseSalary;
		this.birthdate = birthdate;
		this.department = department;
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name.toUpperCase();
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email.toUpperCase();
	}
	
	public Double getBaseSalary() {
		return baseSalary;
	}
	
	public void setBaseSalary(Double baseSalary) {
		this.baseSalary = baseSalary;
	}
	
	public LocalDate getBirthdate() {
		return birthdate;
	}
	
	public void setBirthdate(LocalDate birthdate) {
		this.birthdate = birthdate;
	}
	
	public Department getDepartment() {
		return department;
	}
	
	public void setDepartment(Department department) {
		this.department = department;
	}
	
	@Override
	public String toString() {
		return "Seller [id=" + id + ", name=" + name + ", email=" + email + ", baseSalary=" + nf.format(baseSalary)
				+ ", birthdate=" + sdf.format(birthdate) + ", " + department + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(baseSalary, birthdate, department, email, name);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Seller)) {
			return false;
		}
		Seller other = (Seller) obj;
		return Objects.equals(baseSalary, other.baseSalary) && Objects.equals(birthdate, other.birthdate)
				&& Objects.equals(department, other.department) && Objects.equals(email, other.email)
				&& Objects.equals(name, other.name);
	}
	
}
