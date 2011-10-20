/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Praba Vijayaratnam - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.javadoc.xmltype;

import javax.xml.bind.annotation.*;

@XmlType(propOrder = {})
@XmlRootElement
public class Address2 {

	public String name, street, city, state;

	String getName() {
		return name;
	}

	void setName(String n) {
		this.name = n;
	}

	String getStreet() {
		return street;
	}

	void setStreet(String str) {
		this.street = str;
	}

	String getCity() {
		return city;
	}

	void setCity(String c) {
		this.city = c;
	}

	String getState() {
		return state;
	}

	void setState(String s) {
		this.state = s;
	}

	public boolean equals(Object obj) {
		Address2 addr = (Address2) obj;
		return name.equals(addr.name) && city.equals(addr.city)
				&& street.equals(addr.street) && state.equals(addr.state);
	}
}
