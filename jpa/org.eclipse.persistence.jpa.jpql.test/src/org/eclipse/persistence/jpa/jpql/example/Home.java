package org.eclipse.persistence.jpa.jpql.example;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@SuppressWarnings("unused")
public class Home implements Serializable {

	private List<Phone> phones;
	@Id
	private String city;
}