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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.simpletypes.childcollection;

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
