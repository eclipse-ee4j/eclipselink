/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.simpletypes.childelement;

public class Phone  {
	private String number;

	public Phone() {}
	
	public Phone(String newNumber) {
		number = newNumber;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String newNumber) {
		number = newNumber;
	}

	public String toString() {
		return "Phone(number=" + number + ")";
	}

	public boolean equals(Object object) {
		if(!(object instanceof Phone)) {
			return false;
		}

		if (!((Phone)object).getNumber().equals(this.getNumber())) {
			return false;
		}

		return true;
	}
}
