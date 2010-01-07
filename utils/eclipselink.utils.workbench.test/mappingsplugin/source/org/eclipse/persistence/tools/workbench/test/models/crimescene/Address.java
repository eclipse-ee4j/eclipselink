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
package org.eclipse.persistence.tools.workbench.test.models.crimescene;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;

/**
 * Simple class for storing address information.
 **/
public class Address {
	private String street;
	private String city;
	private String state;
	private String zip;
/**
 * Construct a new Address object
 */
public Address() {
	super();
}
public static void addToDescriptor(ClassDescriptor desc) {
}
/**
 * Return the city of this Address object
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @return java.lang.String
 */
public String getCity() {
	return this.city;
}
/**
 * Return the state of this Address object.
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @return java.lang.String
 */
public String getState() {
	return this.state;
}
/**
 * Return the street address of this Address object
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @return java.lang.String
 */
public String getStreet() {
	return this.street;
}
/**
 * Return the zip code of this Address object
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @return java.lang.String
 */
public String getZip() {
	return this.zip;
}
public static void handleEvents(DescriptorEvent event) {
}
/**
 * Set the city for this address object.
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @param newValue java.lang.String
 */
public void setCity(String newValue) {
	this.city = newValue;
}
/**
 * Set the state for this address object.
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @param newValue java.lang.String
 */
public void setState(String newValue) {
	this.state = newValue;
}
/**
 * Set the street for this address object
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @param newValue java.lang.String
 */
public void setStreet(String newValue) {
	this.street = newValue;
}
/**
 * Set the zip code for this address object
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @param newValue java.lang.String
 */
public void setZip(String newValue) {
	this.zip = newValue;
}
/**
 * Returns a String which contains the entire address on one line.
 * e.g., 2810 Elgin St., Durham, NC 27704
 * @return the address
 */
@Override
public String toString() {
	return getStreet() + ", " + getCity() + ", " + getState() + " " + getZip();
}
}
