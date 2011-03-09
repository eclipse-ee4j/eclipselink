package org.eclipse.persistence.jpa.jpql.example;

import java.util.Date;
import java.util.Map;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@SuppressWarnings("unused")
public class Alias {

	@Id
	private int id;
	private String alias;
	@ElementCollection
	@Temporal(TemporalType.DATE)
	private Map<String, Date> ids;
	private Customer customer;
	@JoinColumn(name="ID", referencedColumnName="ALIAS.ALIAS")
	private Map<Customer, Address> addresses;
}