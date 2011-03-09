package org.eclipse.persistence.jpa.jpql.example;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@SuppressWarnings("unused")
public class Order {

	private BigInteger price;
	private BigDecimal realPrice;
	private double totalPrice;
	@Id private int id;
	private String number;
}