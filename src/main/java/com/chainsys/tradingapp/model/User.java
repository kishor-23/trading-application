package com.chainsys.tradingapp.model;

import java.sql.Blob;
import java.sql.Date;





public class User {
    private int id;
    private String name;
    private String email;
    private String pancardno;
    private String phone;
    private Date dob;
    private String password;
    private Blob profilePicture; 
    private double balance;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPancardno() {
		return pancardno;
	}
	public void setPancardno(String pancardno) {
		this.pancardno = pancardno;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Date getDob() {
		return dob;
	}
	public void setDob(Date dob) {
		this.dob = dob;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Blob getProfilePicture() {
		return profilePicture;
	}
	public void setProfilePicture(Blob profilePicture) {
		this.profilePicture = profilePicture;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
    
    
}
