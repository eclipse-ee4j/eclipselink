package org.eclipse.persistence.jpa.jpql.example;

import java.sql.Date;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@SuppressWarnings("unused")
public class Product extends AbstractProduct {

	@Id private int id;
	private Project project;
	private int quantity;
	private Date releaseDate;
	private ShelfLife shelfLife;
	private EnumType enumType;
}