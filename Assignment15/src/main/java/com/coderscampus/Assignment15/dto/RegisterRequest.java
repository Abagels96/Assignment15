package com.coderscampus.Assignment15.dto;

public class RegisterRequest {
	private String username;
	private String displayName;
	private String password;
	private Integer numChildren;
	private String childNames;
	private String childAges;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getNumChildren() {
		return numChildren;
	}

	public void setNumChildren(Integer numChildren) {
		this.numChildren = numChildren;
	}

	public String getChildNames() {
		return childNames;
	}

	public void setChildNames(String childNames) {
		this.childNames = childNames;
	}

	public String getChildAges() {
		return childAges;
	}

	public void setChildAges(String childAges) {
		this.childAges = childAges;
	}
}


