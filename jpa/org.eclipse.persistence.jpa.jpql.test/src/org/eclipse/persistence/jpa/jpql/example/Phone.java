package org.eclipse.persistence.jpa.jpql.example;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@SuppressWarnings("unused")
public class Phone implements Serializable {

	private String area;
	@ManyToOne
	@JoinColumn(name = "CUSTOMER_ID")
	private Customer customer;
	@ManyToOne
	@JoinColumn(name = "EMPLOYEE_ID")
	private Employee employee;
	@Id
	@Column(nullable = false)
	private Long id;
	@Column(name="PHONE_NUMBER")
	private String phoneNumber;
	private Long type;
}