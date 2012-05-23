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
package org.eclipse.persistence.tools.workbench.test.models.crimescene;

/**
 * Models a simple Person object.
 */
public class Person {
	public Integer id;
	public String firstName;
	public String lastName;
	public int age;
	public String gender;

	private Address address;
/**
 * Construct a new Person object.  Initialize ValueHolder objects if present.
 */
public Person() {
	super();
	setAddress(new Address());
}
/**
 * @author Christopher Garrett
 * @return java.lang.String
 */
public String fullName() {
	return getFirstName() + " " + getLastName();
}
/**
 * Returns a Vector of Strings for genders
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 */
public static java.util.Vector genders() {
	java.util.Vector v = new java.util.Vector();
	v.addElement("Male");
	v.addElement("Female");
	return v;
}
	
	public Address getAddress() { return address; }
/**
 * Return the person's age in years.
 * @return int
 */
public int getAge() {
	return age;
}
/**
 * @author Christopher Garrett
 * @since TopLink for Java 1.1
 * @return java.lang.String
 */
public String getCity() {
	return address.getCity();
}
/**
 * Return this person's first name
 * @return java.lang.String
 */
public String getFirstName() {
	return firstName;
}
/**
 * Return the person's gender as "Male" or "Female"
 * @return java.lang.String
 */
public String getGender() {
	return gender;
}
/**
 * Return the ID for this object
 * @return long
 */
public Integer getId() {
	return id;
}
/**
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @return java.lang.String
 */
public String getLastName() {
	return lastName;
}
/**
 * @author Christopher Garrett
 * @since TopLink for Java 1.1
 * @return java.lang.String
 */
public String getPostalCode() {
	return address.getZip();
}
/**
 * @author Christopher Garrett
 * @since TopLink for Java 1.1
 * @return java.lang.String
 */
public String getState() {
	return address.getState();
}
/**
 * @author Christopher Garrett
 * @since TopLink for Java 1.1
 * @return java.lang.String
 */
public String getStreet() {
	return address.getStreet();
}
	public void setAddress(Address address) {this.address = address;}
/**
 * Set this person's age, in years
 * @param newValue int
 */
public void setAge(int newValue) {
	this.age = newValue;
}
/**
 * @author Christopher Garrett
 * @since TopLink for Java 1.1
 * @param newValue java.lang.String
 */
public void setCity(String newValue) {
	address.setCity(newValue);
}
/**
 * Set the name of this person
 * @param newValue java.lang.String
 */
public void setFirstName(String newValue) {
	this.firstName = newValue;
}
/**
 * Set the gender of this person.  Valid arguments are null, "", "Male" and "Female"
 * @param newValue java.lang.String
 */
public void setGender(String newValue) {
	if (newValue == null || newValue.equals("") || newValue.equals("Male") || newValue.equals("Female")) {
		this.gender = newValue;
	} else {
		throw new IllegalArgumentException("Gender can only be null, empty string, Male, or Female.");
	}
}

public void setId(Integer newValue) 
{
	id = newValue;
}

/**
 * @author Christopher Garrett
 * @since TopLink for Java Course 1.1
 * @param newValue java.lang.String
 */
public void setLastName(String newValue) {
	this.lastName = newValue;
}
/**
 * @author Christopher Garrett
 * @since TopLink for Java 1.1
 * @param newValue java.lang.String
 */
public void setPostalCode(String newValue) {
	address.setZip(newValue);
}
/**
 * @author Christopher Garrett
 * @since TopLink for Java 1.1
 * @param newValue java.lang.String
 */
public void setState(String newValue) {
	address.setState(newValue);
}
/**
 * @author Christopher Garrett
 * @since TopLink for Java 1.1
 * @param newValue java.lang.String
 */
public void setStreet(String newValue) {
	address.setStreet(newValue);
}
/**
 * Returns this person's name
 * @return a string representation of the receiver
 */
@Override
public String toString() {
	return getFirstName() + " " + getLastName();
}
}
