/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.singlekey.nonstringkeytype;

public class Address extends org.eclipse.persistence.testing.oxm.mappings.keybased.Address {
	public int id;
	public String street;
	public String city;
	public String country;
	public String zip;
	
	public Object getKey() {
		return id;
	}
	
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Address)) {
			return false;
		}
		Address tgtAddress = (Address) obj;
		return (tgtAddress.city.equals(city) &&
				tgtAddress.country.equals(country) &&
				tgtAddress.id==id &&
				tgtAddress.street.equals(street) &&
				tgtAddress.zip.equals(zip));
	}
}