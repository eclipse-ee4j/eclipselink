/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3 - initial implementation
 *     Praba Vijayaratnam - 2.3 - test automation
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.jaxrs;

import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.*;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;

@Entity
@XmlRootElement
@Table(name = "ADDRESS")
public class Address implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	private String city;

	private String street;

	@OneToOne
	@PrimaryKeyJoinColumn
	private Customer customer;

	public Address() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStreet() {
		return this.street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	@XmlInverseReference(mappedBy = "address")
	public Customer getCustomer() {
		return this.customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public boolean equals(Object object) {
		if (null == object || object.getClass() != this.getClass()) {
			return false;
		}
		Address test = (Address) object;
		if (!equals(id, test.getId())) {
			return false;
		}
		if (!equals(city, test.getCity())) {
			return false;
		}
		if (!equals(street, test.getStreet())) {
			return false;
		}
		if (null == test.getCustomer() && null != customer) {
			return false;
		}
		if (null != test.getCustomer() && null == customer) {
			return false;
		}
		return true;
	}

	private boolean equals(Object control, Object test) {
		if (null == control) {
			return null == test;
		}
		return control.equals(test);
	}

}