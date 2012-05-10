/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Denise Smith - September 21 /2009
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.norefclass;

public class Address {
	private String street;

	public Address() {
		super();
	}

	public String toString() {
		String returnString = " Address: " + getStreet();

		return returnString;
	}

	public boolean equals(Object object) {
		if (!(object instanceof Address)) {
			return false;
		}
		Address addressObject = (Address) object;
		if (!(this.getStreet().equals(addressObject.getStreet()))) {
			return false;
		}

		return true;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

}
