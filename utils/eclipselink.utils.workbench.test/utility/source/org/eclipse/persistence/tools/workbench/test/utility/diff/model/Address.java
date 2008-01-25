/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.utility.diff.model;

import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

/**
 * 
 */
public class Address {
	private String street;
	private String city;
	private State state;
	private String zip;

	public Address(String street, String city, State state, String zip) {
		super();
		this.street = street;
		this.city = city;
		this.state = state;
		this.zip = zip;
	}

	public String getCity() {
		return this.city;
	}

	public State getState() {
		return this.state;
	}

	public String getStreet() {
		return this.street;
	}

	public String getZip() {
		return this.zip;
	}

	public void setCity(String string) {
		this.city = string;
	}

	public void setState(State state) {
		this.state = state;
	}

	public void setStreet(String string) {
		this.street = string;
	}

	public void setZip(String string) {
		this.zip = string;
	}

	public String toString() {
		return StringTools.buildToStringFor(this, this.street);
	}

}
