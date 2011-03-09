package org.eclipse.persistence.jpa.jpql.example;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
@SuppressWarnings("unused")
public class Dept implements Serializable {
	@Id
	@Column(nullable = false)
	private Long deptno;
	@Column(length = 14)
	private String dname;
	@OneToMany(mappedBy = "dept")
	private List<Employee> empList;
	private int floorNumber;
	@Column(length = 13)
	private String loc;
	private String role;
}