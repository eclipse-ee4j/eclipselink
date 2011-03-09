package org.eclipse.persistence.jpa.jpql.example;

import java.util.Collection;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@SuppressWarnings("unused")
public class CodeAssist {

	private String name;
	private long id;
	@ManyToOne
	private Employee manager;
	private Collection<Employee> employees;
	private Map<Customer, String> customerMap;
	private Map<Customer, Address> customerMapAddress;
}