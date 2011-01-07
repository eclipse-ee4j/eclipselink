/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.test.models.insurance;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/** 
 * <p><b>Purpose</b>: Represents an insurance policy holder.
 * <p><b>Description</b>: Root object that holder an Address and has a 1-M to Policy.
 * @see Policy
 * @since TOPLink/Java 1.0
 */

public class PolicyHolder implements Serializable {
	private String firstName;
	private String lastName;
	private String sex;
	private long ssn;
	private java.sql.Date birthDate;
	private String occupation;
	private Address address;
	private Map policies;
	private Vector childrenNames;


public PolicyHolder () 
{
	this.firstName = "";
	this.lastName = "";
	this.occupation = "";
	this.sex = "Male";
	this.policies = new HashMap(3);
	this.childrenNames = new Vector(2);
}

public void addChildName(String name) 
{
	getChildrenNames().addElement(name);
}
/**
 * Add the policy.
 * Note that it is important to maintain bi-directional relationships both ways when adding.
 */

public Policy addPolicy(Policy policy) 
{
	policies.put(policy, policy);
	policy.setPolicyHolder(this);
	return policy;
}
/**
 * Return an example policy holder instance.
 */

public static PolicyHolder example1() 
{
	PolicyHolder holder = new PolicyHolder();

	holder.setFirstName("Bob");
	holder.setLastName("Smith");
	holder.addChildName("Bobby");
	holder.addChildName("Bessy-Sue");
	holder.addChildName("Bessy-Ray");
	holder.setMale();
	holder.setSsn(1111);
	holder.setBirthDate(new java.sql.Date(50, 1, 30));
	holder.setOccupation("Engineer");
	
	holder.setAddress(Address.example1());
	holder.addPolicy(HealthPolicy.example1());

	return holder;
}
/** 
 * Return an example employee instance.
 */

public static PolicyHolder example2() 
{
	PolicyHolder holder = new PolicyHolder();

	holder.setFirstName("Jill");
	holder.setLastName("May");
	holder.setFemale();
	holder.setSsn(2222);
	holder.setBirthDate(new java.sql.Date(60, 1, 15));
	holder.setOccupation("Diving");
	
	holder.setAddress(Address.example2());
	holder.addPolicy(HousePolicy.example1());
	holder.addPolicy(VehiclePolicy.example2());

	return holder;
}
/** 
 * Return an example employee instance.
 */

public static PolicyHolder example3() 
{
	PolicyHolder holder = new PolicyHolder();

	holder.setFirstName("Sarah");
	holder.setLastName("Way");
	holder.setFemale();
	holder.setSsn(3333);
	holder.setBirthDate(new java.sql.Date(77, 2, 3));
	holder.setOccupation("Student");

	holder.setAddress(Address.example3());
	holder.addPolicy(HousePolicy.example2());

	return holder;
}
/** 
 * Return an example employee instance.
 */

public static PolicyHolder example4() 
{
	PolicyHolder holder = new PolicyHolder();

	holder.setFirstName("Sarah-loo");
	holder.setLastName("Smitty");
	holder.addChildName("Gene");
	holder.addChildName("Jen");
	holder.addChildName("Jess");
	holder.addChildName("Jean");
	holder.setFemale();
	holder.setSsn(4444);
	holder.setBirthDate(new java.sql.Date(19, 8, 9));
	holder.setOccupation("Unemployed");
	
	holder.setAddress(Address.example1());
	holder.addPolicy(VehiclePolicy.example1());

	return holder;
}
/** 
 * Return an example employee instance.
 */

public static PolicyHolder example5() 
{
	PolicyHolder holder = new PolicyHolder();

	holder.setFirstName("Shi");
	holder.setLastName("Shu");
	holder.addChildName("tai");
	holder.addChildName("lin");
	holder.addChildName("ching");
	holder.setFemale();
	holder.setSsn(5555);
	holder.setBirthDate(new java.sql.Date(10, 8, 9));
	holder.setOccupation("Unemployed");
	
	holder.setAddress(Address.example1());
	holder.addPolicy(VehiclePolicy.example3());

	return holder;
}
public Address getAddress() 
{
	return address;
}
public java.sql.Date getBirthDate() 
{
	return birthDate;
}
public Vector getChildrenNames() 
{
	return childrenNames;
}
public String getFirstName () 
{
	return firstName;
}
public String getLastName () 
{
	return lastName;
}
public String getOccupation() 
{
	return occupation;
}
public Map getPolicies() 
{
	return policies;
}
public String getSex() 
{
	return sex;
}
public long getSsn () 
{
	return ssn;
}
/**
 * This is required for TopLink only to primitively set the address without side effects.
 * If method access is used without indirection the set method given to TopLink must not have any side-effects,
 * such as setting the address's policy holder.  This could cause the unit of work problems.
 */

public void internalSetAddress(Address address) 
{
	this.address = address;
}
public boolean isFemale() 
{
	return getSex().equals("Female");
}
public boolean isMale() 
{
	return getSex().equals("Male");
}
/**
 * Remove the policy.
 * Note that it is important to maintain bi-directional relationships both ways when removing.
 */

public Policy removePolicy(Policy policy) 
{
	policies.remove(policy);
	policy.setPolicyHolder(null);
	return policy;
}
/**
 * Set the address.
 * Note that it is important to maintain bi-directional relationships both ways.
 */

public void setAddress(Address address) 
{
	this.address = address;
	if (address != null) {
		address.setPolicyHolder(this);
	}
}
public void setBirthDate(java.sql.Date birthDate) 
{
	this.birthDate = birthDate;
}
public void setChildrenNames(Vector childrenNames) 
{
	this.childrenNames = childrenNames;
}
public void setFemale() 
{
	setSex("Female");
}
public void setFirstName(String firstName) 
{
	this.firstName = firstName;
}
public void setLastName(String lastName) 
{
	this.lastName = lastName;
}
public void setMale() 
{
	setSex("Male");
}
public void setOccupation(String occupation) 
{
	this.occupation = occupation;
}
public void setPolicies(Map policies) 
{
	this.policies = policies;
}
public void setSex(String sex) 
{
	this.sex = sex;
}
public void setSsn(long ssn) 
{
	this.ssn = ssn;
}
@Override
public String toString()
{
	return "PolicyHolder: " + getFirstName() + " " + getLastName();
}

}
