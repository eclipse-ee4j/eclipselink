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
 *     Praba Vijayaratnam - 2.3 - inital implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.jaxrs;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlRootElement
public class Customers implements Serializable {

	private List<Customer> customer;

	public List<Customer> getCustomer() {
		return this.customer;
	}

	public void setCustomer(List<Customer> customerList) {
		this.customer = customerList;
	}

	public boolean equals(Object object) {
		Customers testCustomers = (Customers) object;
		if (testCustomers.getCustomer().size() != 2) {
			return false;
		}

		return ((Customer) (testCustomers.getCustomer().get(0)))
				.equals((Customer) (testCustomers.getCustomer().get(0)))
				&& ((Customer) (testCustomers.getCustomer().get(1)))
						.equals((Customer) (testCustomers.getCustomer().get(1)));

	}

}