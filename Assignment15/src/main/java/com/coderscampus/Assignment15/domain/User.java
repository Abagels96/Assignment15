package com.coderscampus.Assignment15.domain;

	import jakarta.persistence.Entity;
	import jakarta.persistence.GeneratedValue;
	import jakarta.persistence.GenerationType;
	import jakarta.persistence.Id;
	import jakarta.persistence.Table;
	import jakarta.persistence.Column;
	import jakarta.persistence.ManyToMany;
	import jakarta.persistence.OneToMany;
	import java.util.HashSet;
	import java.util.Set;
	import java.util.List;
	import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
	@Table(name = "users")
public class User {
	
		
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

	@Column(nullable = false)
	private Integer numChildren;
	@Column
	private String childNames;
	@Column(nullable = false)
	private String childAges;

    @ManyToMany(mappedBy = "users")
    @JsonIgnore
    private Set<Task> tasks = new HashSet<>();

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Activity> activities;

	
	
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

    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }


		
	    
	    
	    

	}



