package com.coderscampus.Assignment15.domain;

	import jakarta.persistence.Entity;
	import jakarta.persistence.GeneratedValue;
	import jakarta.persistence.GenerationType;
	import jakarta.persistence.Id;
	import jakarta.persistence.Table;
	import jakarta.persistence.Column;
@Entity
	@Table(name = "users")
public class User {
	
		
		@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;
		
		@Column(nullable = false, unique = true)
	private String username;


		@Column(nullable = false)
	private String displayName;

		// Store a hashed password (BCrypt), not plaintext
		@Column(nullable = false)
	private String password;
		  public Long getUserId() {
			return userId;
		}


		public void setUserId(Long userId) {
			this.userId = userId;
		}


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


		
	    
	    
	    

	}



