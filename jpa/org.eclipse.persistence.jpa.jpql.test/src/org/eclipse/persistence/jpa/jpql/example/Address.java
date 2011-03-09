package org.eclipse.persistence.jpa.jpql.example;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
@SuppressWarnings("unused")
public class Address implements Serializable {

	private String city;
	@Id
	@Column(nullable = false)
	@GeneratedValue(generator="mySequenceGeneratorName")
	private Long id;
	private String state;
	private String street;
	@Embedded
	private ZipCode zip;
	@OneToMany(mappedBy = "address")
	private List<Customer> customerList;
}