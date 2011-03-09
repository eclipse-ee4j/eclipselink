package org.eclipse.persistence.jpa.jpql.example;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

@Entity
@SuppressWarnings("unused")
public class Customer implements Serializable {
	@Column(name="FIRST_NAME")
	private String firstName;
	@Column(name="HAS_GOOD_CREDIT")
	private Long hasGoodCredit;
	@Id
	@Column(nullable = false)
	private Long id;
	@Column(name="LAST_NAME")
	private String lastName;
	@ManyToOne
	@JoinColumn(name = "ADDRESS_ID")
	private Address address;
	@OneToMany(mappedBy = "customer")
	private List<Phone> phoneList;
	@OrderBy
	@OneToMany(mappedBy = "customer")
	private List<Alias> aliases;
	private Home home;
	@Transient
	private String title;
	private Dept dept;
}