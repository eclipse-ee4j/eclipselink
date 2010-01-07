/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.test.models.simpleaggregate;

/**
 * Insert the type's description here.
 * Creation date: (11/6/2000 8:52:26 AM)
 * @author: Christopher Garrett
 */
public class AddressSubClass extends Address {
	private String country;
/**
 * AddressSubClass constructor comment.
 */
public AddressSubClass() {
	super();
}
public String getCountry() {
	return this.country;
}
public void setCountry(String newCountry) {
	this.country = newCountry;
}
@Override
public String toString() {
	return super.toString() + " " + this.country;
}
}
