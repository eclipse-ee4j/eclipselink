package org.eclipse.persistence.jpa.jpql.example;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
@SuppressWarnings("unused")
public class Project {

	private String name;
	@Id private float id;
	@ManyToMany
	private List<Employee> employees;
	private boolean completed;
}