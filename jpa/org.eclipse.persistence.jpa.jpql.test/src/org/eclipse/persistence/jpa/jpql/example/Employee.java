package org.eclipse.persistence.jpa.jpql.example;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
@SuppressWarnings("unused")
public class Employee implements Serializable {

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY)
	@JoinColumn(name = "ADDR_ID")
	private Address address;
	private String department;
	@ManyToOne
	@JoinColumn(name = "DEPTNO")
	private Dept dept;
	@Id
	@Column(name = "EMP_ID", nullable = false)
	private Long empId;
	private String manager;
	@ManyToOne(cascade=CascadeType.PERSIST, fetch=FetchType.LAZY)
	private Employee managerEmployee;
	private String name;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "employee")
	private Collection<Phone> phoneNumbers;
	private int roomNumber;
	private Long salary;
	private Boolean working;
}