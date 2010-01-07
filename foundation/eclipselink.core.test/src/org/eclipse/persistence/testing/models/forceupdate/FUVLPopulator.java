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
package org.eclipse.persistence.testing.models.forceupdate;

import java.util.*;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;

/** 
 * <p><b>Purpose</b>: To build and populate the database for example and testing purposes.
 * This population routine is fairly complex and makes use of the population manager to
 * resolve interrated objects as the employeeTLIC objects are an interconnection graph of objects.
 *
 * This is not the recomended way to create new objects in your application,
 * this is just the easiest way to create interconnected new example objects from code.
 * Normally in your application the objects will be defined as part of a transactional and user interactive process.
 */

public class FUVLPopulator {
	protected PopulationManager populationManager;
public FUVLPopulator ( ) 
{
	this.populationManager = PopulationManager.getDefaultManager();
}
public AddressTLIC addressTLICExample1() 
{
	AddressTLIC address = new AddressTLIC();
	
	address.setCity("Toronto");
	address.setPostalCode("L5J2B5");
	address.setProvince("ONT");
	address.setStreet("1450 Acme Cr., suite 4");
	address.setCountry("Canada");
	return address;
}
public AddressTLIC addressTLICExample10() 
{
	AddressTLIC address = new AddressTLIC();

	address.setCity("Calgary");
	address.setPostalCode("J5J2B5");
	address.setProvince("ALB");
	address.setStreet("1111 Moose Rd.");
	address.setCountry("Canada");
	return address;
}
public AddressTLIC addressTLICExample11() 
{
	AddressTLIC address = new AddressTLIC();
	
	address.setCity("Arnprior");
	address.setPostalCode("W1A2B5");
	address.setProvince("ONT");
	address.setStreet("1 Nowhere Drive");
	address.setCountry("Canada");
	return address;
}
public AddressTLIC addressTLICExample12() 
{
	AddressTLIC address = new AddressTLIC();
	
	address.setCity("Yellow Knife");
	address.setPostalCode("Y5J2N5");
	address.setProvince("YK");
	address.setStreet("1112 Gold Rush rd.");
	address.setCountry("Canada");
	return address;
}
public AddressTLIC addressTLICExample2() 
{
	AddressTLIC address = new AddressTLIC();
	
	address.setCity("Ottawa");
	address.setPostalCode("K5J2B5");
	address.setProvince("ONT");
	address.setStreet("12 Merival Rd., suite 5");
	address.setCountry("Canada");
	return address;
}
public AddressTLIC addressTLICExample3() 
{
	AddressTLIC address = new AddressTLIC();
	
	address.setCity("Perth");
	address.setPostalCode("Y3Q2N9");
	address.setProvince("ONT");
	address.setStreet("234 I'm Lost Lane");
	address.setCountry("Canada");
	return address;
}
public AddressTLIC addressTLICExample4() 
{
	AddressTLIC address = new AddressTLIC();

	address.setCity("Prince Rupert");
	address.setPostalCode("K3k5DD");
	address.setProvince("BC");
	address.setStreet("3254 Real Cold Place");
	address.setCountry("Canada");
	return address;
}
public AddressTLIC addressTLICExample5() 
{
	AddressTLIC address = new AddressTLIC();

	address.setCity("Vancouver");
	address.setPostalCode("N5J2N5");
	address.setProvince("BC");
	address.setStreet("1111 Mountain Blvd. Floor 53, suite 6");
	address.setCountry("Canada");
	return address;
}
public AddressTLIC addressTLICExample6() 
{
	AddressTLIC address = new AddressTLIC();
	
	address.setCity("Montreal");
	address.setPostalCode("Q2S5Z5");
	address.setProvince("QUE");
	address.setStreet("1 Habs Place");
	address.setCountry("Canada");
	return address;
}
public AddressTLIC addressTLICExample7() 
{
	AddressTLIC address = new AddressTLIC();
	
	address.setCity("Metcalfe");
	address.setPostalCode("Y4F7V6");
	address.setProvince("ONT");
	address.setStreet("2 Anderson Rd.");
	address.setCountry("Canada");
	return address;
}
public AddressTLIC addressTLICExample8() 
{
	AddressTLIC address = new AddressTLIC();
	
	address.setCity("Victoria");
	address.setPostalCode("Z5J2N5");
	address.setProvince("BC");
	address.setStreet("382 Hyde Park");
	address.setCountry("Canada");
	return address;
}
public AddressTLIC addressTLICExample9() 
{
	AddressTLIC address = new AddressTLIC();
	
	address.setCity("Smith Falls");
	address.setPostalCode("C6C6C6");
	address.setProvince("ONT");
	address.setStreet("1 Chocolate Drive");
	address.setCountry("Canada");
	return address;
}
public AddressTLIO addressTLIOExample1() 
{
	AddressTLIO address = new AddressTLIO();
	
	address.setCity("Toronto");
	address.setPostalCode("L5J2B5");
	address.setProvince("ONT");
	address.setStreet("1450 Acme Cr., suite 4");
	address.setCountry("Canada");
	return address;
}
public AddressTLIO addressTLIOExample10() 
{
	AddressTLIO address = new AddressTLIO();

	address.setCity("Calgary");
	address.setPostalCode("J5J2B5");
	address.setProvince("ALB");
	address.setStreet("1111 Moose Rd.");
	address.setCountry("Canada");
	return address;
}
public AddressTLIO addressTLIOExample11() 
{
	AddressTLIO address = new AddressTLIO();
	
	address.setCity("Arnprior");
	address.setPostalCode("W1A2B5");
	address.setProvince("ONT");
	address.setStreet("1 Nowhere Drive");
	address.setCountry("Canada");
	return address;
}
public AddressTLIO addressTLIOExample12() 
{
	AddressTLIO address = new AddressTLIO();
	
	address.setCity("Yellow Knife");
	address.setPostalCode("Y5J2N5");
	address.setProvince("YK");
	address.setStreet("1112 Gold Rush rd.");
	address.setCountry("Canada");
	return address;
}
public AddressTLIO addressTLIOExample2() 
{
	AddressTLIO address = new AddressTLIO();
	
	address.setCity("Ottawa");
	address.setPostalCode("K5J2B5");
	address.setProvince("ONT");
	address.setStreet("12 Merival Rd., suite 5");
	address.setCountry("Canada");
	return address;
}
public AddressTLIO addressTLIOExample3() 
{
	AddressTLIO address = new AddressTLIO();
	
	address.setCity("Perth");
	address.setPostalCode("Y3Q2N9");
	address.setProvince("ONT");
	address.setStreet("234 I'm Lost Lane");
	address.setCountry("Canada");
	return address;
}
public AddressTLIO addressTLIOExample4() 
{
	AddressTLIO address = new AddressTLIO();

	address.setCity("Prince Rupert");
	address.setPostalCode("K3k5DD");
	address.setProvince("BC");
	address.setStreet("3254 Real Cold Place");
	address.setCountry("Canada");
	return address;
}
public AddressTLIO addressTLIOExample5() 
{
	AddressTLIO address = new AddressTLIO();

	address.setCity("Vancouver");
	address.setPostalCode("N5J2N5");
	address.setProvince("BC");
	address.setStreet("1111 Mountain Blvd. Floor 53, suite 6");
	address.setCountry("Canada");
	return address;
}
public AddressTLIO addressTLIOExample6() 
{
	AddressTLIO address = new AddressTLIO();
	
	address.setCity("Montreal");
	address.setPostalCode("Q2S5Z5");
	address.setProvince("QUE");
	address.setStreet("1 Habs Place");
	address.setCountry("Canada");
	return address;
}
public AddressTLIO addressTLIOExample7() 
{
	AddressTLIO address = new AddressTLIO();
	
	address.setCity("Metcalfe");
	address.setPostalCode("Y4F7V6");
	address.setProvince("ONT");
	address.setStreet("2 Anderson Rd.");
	address.setCountry("Canada");
	return address;
}
public AddressTLIO addressTLIOExample8() 
{
	AddressTLIO address = new AddressTLIO();
	
	address.setCity("Victoria");
	address.setPostalCode("Z5J2N5");
	address.setProvince("BC");
	address.setStreet("382 Hyde Park");
	address.setCountry("Canada");
	return address;
}
public AddressTLIO addressTLIOExample9() 
{
	AddressTLIO address = new AddressTLIO();
	
	address.setCity("Smith Falls");
	address.setPostalCode("C6C6C6");
	address.setProvince("ONT");
	address.setStreet("1 Chocolate Drive");
	address.setCountry("Canada");
	return address;
}
public AddressVLIC addressVLICExample1() 
{
	AddressVLIC address = new AddressVLIC();
	
	address.setCity("Toronto");
	address.setPostalCode("L5J2B5");
	address.setProvince("ONT");
	address.setStreet("1450 Acme Cr., suite 4");
	address.setCountry("Canada");
	return address;
}
public AddressVLIC addressVLICExample10() 
{
	AddressVLIC address = new AddressVLIC();

	address.setCity("Calgary");
	address.setPostalCode("J5J2B5");
	address.setProvince("ALB");
	address.setStreet("1111 Moose Rd.");
	address.setCountry("Canada");
	return address;
}
public AddressVLIC addressVLICExample11() 
{
	AddressVLIC address = new AddressVLIC();
	
	address.setCity("Arnprior");
	address.setPostalCode("W1A2B5");
	address.setProvince("ONT");
	address.setStreet("1 Nowhere Drive");
	address.setCountry("Canada");
	return address;
}
public AddressVLIC addressVLICExample12() 
{
	AddressVLIC address = new AddressVLIC();
	
	address.setCity("Yellow Knife");
	address.setPostalCode("Y5J2N5");
	address.setProvince("YK");
	address.setStreet("1112 Gold Rush rd.");
	address.setCountry("Canada");
	return address;
}
public AddressVLIC addressVLICExample2() 
{
	AddressVLIC address = new AddressVLIC();
	
	address.setCity("Ottawa");
	address.setPostalCode("K5J2B5");
	address.setProvince("ONT");
	address.setStreet("12 Merival Rd., suite 5");
	address.setCountry("Canada");
	return address;
}
public AddressVLIC addressVLICExample3() 
{
	AddressVLIC address = new AddressVLIC();
	
	address.setCity("Perth");
	address.setPostalCode("Y3Q2N9");
	address.setProvince("ONT");
	address.setStreet("234 I'm Lost Lane");
	address.setCountry("Canada");
	return address;
}
public AddressVLIC addressVLICExample4() 
{
	AddressVLIC address = new AddressVLIC();

	address.setCity("Prince Rupert");
	address.setPostalCode("K3k5DD");
	address.setProvince("BC");
	address.setStreet("3254 Real Cold Place");
	address.setCountry("Canada");
	return address;
}
public AddressVLIC addressVLICExample5() 
{
	AddressVLIC address = new AddressVLIC();

	address.setCity("Vancouver");
	address.setPostalCode("N5J2N5");
	address.setProvince("BC");
	address.setStreet("1111 Mountain Blvd. Floor 53, suite 6");
	address.setCountry("Canada");
	return address;
}
public AddressVLIC addressVLICExample6() 
{
	AddressVLIC address = new AddressVLIC();
	
	address.setCity("Montreal");
	address.setPostalCode("Q2S5Z5");
	address.setProvince("QUE");
	address.setStreet("1 Habs Place");
	address.setCountry("Canada");
	return address;
}
public AddressVLIC addressVLICExample7() 
{
	AddressVLIC address = new AddressVLIC();
	
	address.setCity("Metcalfe");
	address.setPostalCode("Y4F7V6");
	address.setProvince("ONT");
	address.setStreet("2 Anderson Rd.");
	address.setCountry("Canada");
	return address;
}
public AddressVLIC addressVLICExample8() 
{
	AddressVLIC address = new AddressVLIC();
	
	address.setCity("Victoria");
	address.setPostalCode("Z5J2N5");
	address.setProvince("BC");
	address.setStreet("382 Hyde Park");
	address.setCountry("Canada");
	return address;
}
public AddressVLIC addressVLICExample9() 
{
	AddressVLIC address = new AddressVLIC();
	
	address.setCity("Smith Falls");
	address.setPostalCode("C6C6C6");
	address.setProvince("ONT");
	address.setStreet("1 Chocolate Drive");
	address.setCountry("Canada");
	return address;
}
public AddressVLIO addressVLIOExample1() 
{
	AddressVLIO address = new AddressVLIO();
	
	address.setCity("Toronto");
	address.setPostalCode("L5J2B5");
	address.setProvince("ONT");
	address.setStreet("1450 Acme Cr., suite 4");
	address.setCountry("Canada");
	return address;
}
public AddressVLIO addressVLIOExample10() 
{
	AddressVLIO address = new AddressVLIO();

	address.setCity("Calgary");
	address.setPostalCode("J5J2B5");
	address.setProvince("ALB");
	address.setStreet("1111 Moose Rd.");
	address.setCountry("Canada");
	return address;
}
public AddressVLIO addressVLIOExample11() 
{
	AddressVLIO address = new AddressVLIO();
	
	address.setCity("Arnprior");
	address.setPostalCode("W1A2B5");
	address.setProvince("ONT");
	address.setStreet("1 Nowhere Drive");
	address.setCountry("Canada");
	return address;
}
public AddressVLIO addressVLIOExample12() 
{
	AddressVLIO address = new AddressVLIO();
	
	address.setCity("Yellow Knife");
	address.setPostalCode("Y5J2N5");
	address.setProvince("YK");
	address.setStreet("1112 Gold Rush rd.");
	address.setCountry("Canada");
	return address;
}
public AddressVLIO addressVLIOExample2() 
{
	AddressVLIO address = new AddressVLIO();
	
	address.setCity("Ottawa");
	address.setPostalCode("K5J2B5");
	address.setProvince("ONT");
	address.setStreet("12 Merival Rd., suite 5");
	address.setCountry("Canada");
	return address;
}
public AddressVLIO addressVLIOExample3() 
{
	AddressVLIO address = new AddressVLIO();
	
	address.setCity("Perth");
	address.setPostalCode("Y3Q2N9");
	address.setProvince("ONT");
	address.setStreet("234 I'm Lost Lane");
	address.setCountry("Canada");
	return address;
}
public AddressVLIO addressVLIOExample4() 
{
	AddressVLIO address = new AddressVLIO();

	address.setCity("Prince Rupert");
	address.setPostalCode("K3k5DD");
	address.setProvince("BC");
	address.setStreet("3254 Real Cold Place");
	address.setCountry("Canada");
	return address;
}
public AddressVLIO addressVLIOExample5() 
{
	AddressVLIO address = new AddressVLIO();

	address.setCity("Vancouver");
	address.setPostalCode("N5J2N5");
	address.setProvince("BC");
	address.setStreet("1111 Mountain Blvd. Floor 53, suite 6");
	address.setCountry("Canada");
	return address;
}
public AddressVLIO addressVLIOExample6() 
{
	AddressVLIO address = new AddressVLIO();
	
	address.setCity("Montreal");
	address.setPostalCode("Q2S5Z5");
	address.setProvince("QUE");
	address.setStreet("1 Habs Place");
	address.setCountry("Canada");
	return address;
}
public AddressVLIO addressVLIOExample7() 
{
	AddressVLIO address = new AddressVLIO();
	
	address.setCity("Metcalfe");
	address.setPostalCode("Y4F7V6");
	address.setProvince("ONT");
	address.setStreet("2 Anderson Rd.");
	address.setCountry("Canada");
	return address;
}
public AddressVLIO addressVLIOExample8() 
{
	AddressVLIO address = new AddressVLIO();
	
	address.setCity("Victoria");
	address.setPostalCode("Z5J2N5");
	address.setProvince("BC");
	address.setStreet("382 Hyde Park");
	address.setCountry("Canada");
	return address;
}
public AddressVLIO addressVLIOExample9() 
{
	AddressVLIO address = new AddressVLIO();
	
	address.setCity("Smith Falls");
	address.setPostalCode("C6C6C6");
	address.setProvince("ONT");
	address.setStreet("1 Chocolate Drive");
	address.setCountry("Canada");
	return address;
}

/** 
*	Call all of the example methods in this system to guarantee that all our objects
*	are registered in the population manager
*/
public void buildExamples() 
{
	// First ensure that no preivous examples are hanging around.
	PopulationManager.getDefaultManager().getRegisteredObjects().remove(EmployeeTLIC.class);
	PopulationManager.getDefaultManager().getRegisteredObjects().remove(EmployeeTLIO.class);
	PopulationManager.getDefaultManager().getRegisteredObjects().remove(EmployeeVLIC.class);
	PopulationManager.getDefaultManager().getRegisteredObjects().remove(EmployeeVLIO.class);
	
	employeeTLICExample1();
	employeeTLICExample2();
	employeeTLICExample3();
	employeeTLICExample4();
	employeeTLICExample5();
	employeeTLICExample6();
	employeeTLICExample7();
	employeeTLICExample8();
	employeeTLICExample9();
	employeeTLICExample10();
	employeeTLICExample11();
	employeeTLICExample12();
	employeeTLIOExample1();
	employeeTLIOExample2();
	employeeTLIOExample3();
	employeeTLIOExample4();
	employeeTLIOExample5();
	employeeTLIOExample6();
	employeeTLIOExample7();
	employeeTLIOExample8();
	employeeTLIOExample9();
	employeeTLIOExample10();
	employeeTLIOExample11();
	employeeTLIOExample12();
	employeeVLICExample1();
	employeeVLICExample2();
	employeeVLICExample3();
	employeeVLICExample4();
	employeeVLICExample5();
	employeeVLICExample6();
	employeeVLICExample7();
	employeeVLICExample8();
	employeeVLICExample9();
	employeeVLICExample10();
	employeeVLICExample11();
	employeeVLICExample12();
	employeeVLIOExample1();
	employeeVLIOExample2();
	employeeVLIOExample3();
	employeeVLIOExample4();
	employeeVLIOExample5();
	employeeVLIOExample6();
	employeeVLIOExample7();
	employeeVLIOExample8();
	employeeVLIOExample9();
	employeeVLIOExample10();
	employeeVLIOExample11();
	employeeVLIOExample12();

}
protected boolean containsObject(Class domainClass, String identifier) 
{
	return populationManager.containsObject(domainClass, identifier);
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIC employeeTLICExample1()
{
	if (containsObject(EmployeeTLIC.class, "0001")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIC) getObject(EmployeeTLIC.class, "0001");
	}
	
	org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIC employeeTLIC = new EmployeeTLIC();
	
	try {
		
	employeeTLIC.setFirstName("Bob");
	employeeTLIC.setLastName("Smith");
	employeeTLIC.setMale();
	employeeTLIC.setSalary(35000);	
	employeeTLIC.setAddress(addressTLICExample1());
	employeeTLIC.addPhoneNumber(phoneNumberTLICExample1());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};

	registerObject(EmployeeTLIC.class, employeeTLIC, "0001");

	return employeeTLIC;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIC employeeTLICExample10()
{
	if (containsObject(EmployeeTLIC.class, "0010")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIC) getObject(EmployeeTLIC.class, "0010");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIC employeeTLIC = new EmployeeTLIC();

	try {
		
	employeeTLIC.setFirstName("Jill");
	employeeTLIC.setLastName("May");
	employeeTLIC.setFemale();
	employeeTLIC.setAddress(addressTLICExample10());
	employeeTLIC.setSalary(56232);
	employeeTLIC.addPhoneNumber(phoneNumberTLICExample1());
	employeeTLIC.addPhoneNumber(phoneNumberTLICExample2());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeTLIC.class, employeeTLIC, "0010");

	return employeeTLIC;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIC employeeTLICExample11()
{
	if (containsObject(EmployeeTLIC.class, "0011")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIC) getObject(EmployeeTLIC.class, "0011");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIC employeeTLIC = new EmployeeTLIC();

	try {
		
	employeeTLIC.setFirstName("Sarah-loo");
	employeeTLIC.setLastName("Smitty");
	employeeTLIC.setFemale();
	employeeTLIC.setAddress(addressTLICExample11());
	employeeTLIC.setSalary(75000);
	employeeTLIC.addPhoneNumber(phoneNumberTLICExample2());
	employeeTLIC.addPhoneNumber(phoneNumberTLICExample3());
	employeeTLIC.addPhoneNumber(phoneNumberTLICExample4());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeTLIC.class, employeeTLIC, "0011");

	return employeeTLIC;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIC employeeTLICExample12()
{
	if (containsObject(EmployeeTLIC.class, "0012")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIC) getObject(EmployeeTLIC.class, "0012");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIC employeeTLIC = new EmployeeTLIC();

	try {
		
	employeeTLIC.setFirstName("Jim-bob");
	employeeTLIC.setLastName("Jefferson");
	employeeTLIC.setMale();
	employeeTLIC.setAddress(addressTLICExample12());
	employeeTLIC.setSalary(50000);
	employeeTLIC.addPhoneNumber(phoneNumberTLICExample3());
	employeeTLIC.addPhoneNumber(phoneNumberTLICExample4());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeTLIC.class, employeeTLIC, "0012");

	return employeeTLIC;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIC employeeTLICExample2()
{
	if (containsObject(EmployeeTLIC.class, "0002")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIC) getObject(EmployeeTLIC.class, "0002");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIC employeeTLIC = new EmployeeTLIC();

	try {
		
	employeeTLIC.setFirstName("John");
	employeeTLIC.setLastName("Way");
	employeeTLIC.setMale();
	employeeTLIC.setSalary(53000);
	employeeTLIC.setAddress(addressTLICExample2());
	employeeTLIC.addPhoneNumber(phoneNumberTLICExample1());
	employeeTLIC.addPhoneNumber(phoneNumberTLICExample6());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeTLIC.class, employeeTLIC, "0002");

	return employeeTLIC;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIC employeeTLICExample3()
{
	if (containsObject(EmployeeTLIC.class, "0003")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIC) getObject(EmployeeTLIC.class, "0003");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIC employeeTLIC = new EmployeeTLIC();

	try {
		
	employeeTLIC.setFirstName("Charles");
	employeeTLIC.setLastName("Chanley");
	employeeTLIC.setMale();
	employeeTLIC.setSalary(43000);
	employeeTLIC.setAddress(addressTLICExample6());
	employeeTLIC.addPhoneNumber(phoneNumberTLICExample5());
	employeeTLIC.addPhoneNumber(phoneNumberTLICExample6());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeTLIC.class, employeeTLIC, "0003");

	return employeeTLIC;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIC employeeTLICExample4()
{
	if (containsObject(EmployeeTLIC.class, "0004")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIC) getObject(EmployeeTLIC.class, "0004");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIC employeeTLIC = new EmployeeTLIC();

	try {
		
	employeeTLIC.setFirstName("Emanual");
	employeeTLIC.setLastName("Smith");
	employeeTLIC.setMale();
	employeeTLIC.setSalary(49631);
	employeeTLIC.setAddress(addressTLICExample5());
	employeeTLIC.addPhoneNumber(phoneNumberTLICExample2());
	employeeTLIC.addPhoneNumber(phoneNumberTLICExample4());
	employeeTLIC.addPhoneNumber(phoneNumberTLICExample5());
	employeeTLIC.addPhoneNumber(phoneNumberTLICExample6());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeTLIC.class, employeeTLIC, "0004");

	return employeeTLIC;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIC employeeTLICExample5()
{
	if (containsObject(EmployeeTLIC.class, "0005")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIC) getObject(EmployeeTLIC.class, "0005");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIC employeeTLIC = new EmployeeTLIC();

	try {
		
	employeeTLIC.setFirstName("Sarah");
	employeeTLIC.setLastName("Way");
	employeeTLIC.setFemale();
	employeeTLIC.setSalary(87000);
	employeeTLIC.setAddress(addressTLICExample4());
	employeeTLIC.addPhoneNumber(phoneNumberTLICExample1());
	employeeTLIC.addPhoneNumber(phoneNumberTLICExample6());
	employeeTLIC.addPhoneNumber(phoneNumberTLICExample3());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeTLIC.class, employeeTLIC, "0005");

	return employeeTLIC;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIC employeeTLICExample6()
{
	if (containsObject(EmployeeTLIC.class, "0006")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIC) getObject(EmployeeTLIC.class, "0006");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIC employeeTLIC = new EmployeeTLIC();

	try {
		
	employeeTLIC.setFirstName("Marcus");
	employeeTLIC.setLastName("Saunders");
	employeeTLIC.setMale();
	employeeTLIC.setSalary(54300);
	employeeTLIC.setAddress(addressTLICExample3());
	employeeTLIC.addPhoneNumber(phoneNumberTLICExample6());
	employeeTLIC.addPhoneNumber(phoneNumberTLICExample1());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeTLIC.class, employeeTLIC, "0006");

	return employeeTLIC;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIC employeeTLICExample7()
{
	if (containsObject(EmployeeTLIC.class, "0007")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIC) getObject(EmployeeTLIC.class, "0007");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIC employeeTLIC = new EmployeeTLIC();

	try {
		
	employeeTLIC.setFirstName("Nancy");
	employeeTLIC.setLastName("White");
	employeeTLIC.setFemale();
	employeeTLIC.setSalary(31000);
	employeeTLIC.setAddress(addressTLICExample7());
	employeeTLIC.addPhoneNumber(phoneNumberTLICExample3());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeTLIC.class, employeeTLIC, "0007");

	return employeeTLIC;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIC employeeTLICExample8()
{
	if (containsObject(EmployeeTLIC.class, "0008")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIC) getObject(EmployeeTLIC.class, "0008");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIC employeeTLIC = new EmployeeTLIC();

	try {
		
	employeeTLIC.setFirstName("Fred");
	employeeTLIC.setLastName("Jones");
	employeeTLIC.setMale();
	employeeTLIC.setSalary(500000);
	employeeTLIC.setAddress(addressTLICExample8());
	employeeTLIC.addPhoneNumber(phoneNumberTLICExample4());
	employeeTLIC.addPhoneNumber(phoneNumberTLICExample6());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeTLIC.class, employeeTLIC, "0008");

	return employeeTLIC;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIC employeeTLICExample9()
{
	if (containsObject(EmployeeTLIC.class, "0009")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIC) getObject(EmployeeTLIC.class, "0009");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIC employeeTLIC = new EmployeeTLIC();

	try {
		
	employeeTLIC.setFirstName("Betty");
	employeeTLIC.setLastName("Jones");
	employeeTLIC.setFemale();
	employeeTLIC.setSalary(500001);
	employeeTLIC.setAddress(addressTLICExample9());

	employeeTLIC.addPhoneNumber(phoneNumberTLICExample1());
	employeeTLIC.addPhoneNumber(phoneNumberTLICExample6());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeTLIC.class, employeeTLIC, "0009");

	return employeeTLIC;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIO employeeTLIOExample1()
{
	if (containsObject(EmployeeTLIO.class, "0001")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIO) getObject(EmployeeTLIO.class, "0001");
	}
	
	org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIO employeeTLIO = new EmployeeTLIO();
	
	try {
		
	employeeTLIO.setFirstName("Bob");
	employeeTLIO.setLastName("Smith");
	employeeTLIO.setMale();
	employeeTLIO.setSalary(35000);	
	employeeTLIO.setAddress(addressTLIOExample1());
	employeeTLIO.addPhoneNumber(phoneNumberTLIOExample1());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};

	registerObject(EmployeeTLIO.class, employeeTLIO, "0001");

	return employeeTLIO;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIO employeeTLIOExample10()
{
	if (containsObject(EmployeeTLIO.class, "0010")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIO) getObject(EmployeeTLIO.class, "0010");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIO employeeTLIO = new EmployeeTLIO();

	try {
		
	employeeTLIO.setFirstName("Jill");
	employeeTLIO.setLastName("May");
	employeeTLIO.setFemale();
	employeeTLIO.setAddress(addressTLIOExample10());
	employeeTLIO.setSalary(56232);
	employeeTLIO.addPhoneNumber(phoneNumberTLIOExample1());
	employeeTLIO.addPhoneNumber(phoneNumberTLIOExample2());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeTLIO.class, employeeTLIO, "0010");

	return employeeTLIO;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIO employeeTLIOExample11()
{
	if (containsObject(EmployeeTLIO.class, "0011")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIO) getObject(EmployeeTLIO.class, "0011");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIO employeeTLIO = new EmployeeTLIO();

	try {
		
	employeeTLIO.setFirstName("Sarah-loo");
	employeeTLIO.setLastName("Smitty");
	employeeTLIO.setFemale();
	employeeTLIO.setAddress(addressTLIOExample11());
	employeeTLIO.setSalary(75000);
	employeeTLIO.addPhoneNumber(phoneNumberTLIOExample2());
	employeeTLIO.addPhoneNumber(phoneNumberTLIOExample3());
	employeeTLIO.addPhoneNumber(phoneNumberTLIOExample4());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeTLIO.class, employeeTLIO, "0011");

	return employeeTLIO;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIO employeeTLIOExample12()
{
	if (containsObject(EmployeeTLIO.class, "0012")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIO) getObject(EmployeeTLIO.class, "0012");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIO employeeTLIO = new EmployeeTLIO();

	try {
		
	employeeTLIO.setFirstName("Jim-bob");
	employeeTLIO.setLastName("Jefferson");
	employeeTLIO.setMale();
	employeeTLIO.setAddress(addressTLIOExample12());
	employeeTLIO.setSalary(50000);
	employeeTLIO.addPhoneNumber(phoneNumberTLIOExample3());
	employeeTLIO.addPhoneNumber(phoneNumberTLIOExample4());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeTLIO.class, employeeTLIO, "0012");

	return employeeTLIO;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIO employeeTLIOExample2()
{
	if (containsObject(EmployeeTLIO.class, "0002")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIO) getObject(EmployeeTLIO.class, "0002");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIO employeeTLIO = new EmployeeTLIO();

	try {
		
	employeeTLIO.setFirstName("John");
	employeeTLIO.setLastName("Way");
	employeeTLIO.setMale();
	employeeTLIO.setSalary(53000);
	employeeTLIO.setAddress(addressTLIOExample2());
	employeeTLIO.addPhoneNumber(phoneNumberTLIOExample1());
	employeeTLIO.addPhoneNumber(phoneNumberTLIOExample6());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeTLIO.class, employeeTLIO, "0002");

	return employeeTLIO;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIO employeeTLIOExample3()
{
	if (containsObject(EmployeeTLIO.class, "0003")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIO) getObject(EmployeeTLIO.class, "0003");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIO employeeTLIO = new EmployeeTLIO();

	try {
		
	employeeTLIO.setFirstName("Charles");
	employeeTLIO.setLastName("Chanley");
	employeeTLIO.setMale();
	employeeTLIO.setSalary(43000);
	employeeTLIO.setAddress(addressTLIOExample6());
	employeeTLIO.addPhoneNumber(phoneNumberTLIOExample5());
	employeeTLIO.addPhoneNumber(phoneNumberTLIOExample6());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeTLIO.class, employeeTLIO, "0003");

	return employeeTLIO;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIO employeeTLIOExample4()
{
	if (containsObject(EmployeeTLIO.class, "0004")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIO) getObject(EmployeeTLIO.class, "0004");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIO employeeTLIO = new EmployeeTLIO();

	try {
		
	employeeTLIO.setFirstName("Emanual");
	employeeTLIO.setLastName("Smith");
	employeeTLIO.setMale();
	employeeTLIO.setSalary(49631);
	employeeTLIO.setAddress(addressTLIOExample5());
	employeeTLIO.addPhoneNumber(phoneNumberTLIOExample2());
	employeeTLIO.addPhoneNumber(phoneNumberTLIOExample4());
	employeeTLIO.addPhoneNumber(phoneNumberTLIOExample5());
	employeeTLIO.addPhoneNumber(phoneNumberTLIOExample6());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeTLIO.class, employeeTLIO, "0004");

	return employeeTLIO;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIO employeeTLIOExample5()
{
	if (containsObject(EmployeeTLIO.class, "0005")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIO) getObject(EmployeeTLIO.class, "0005");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIO employeeTLIO = new EmployeeTLIO();

	try {
		
	employeeTLIO.setFirstName("Sarah");
	employeeTLIO.setLastName("Way");
	employeeTLIO.setFemale();
	employeeTLIO.setSalary(87000);
	employeeTLIO.setAddress(addressTLIOExample4());
	employeeTLIO.addPhoneNumber(phoneNumberTLIOExample1());
	employeeTLIO.addPhoneNumber(phoneNumberTLIOExample6());
	employeeTLIO.addPhoneNumber(phoneNumberTLIOExample3());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeTLIO.class, employeeTLIO, "0005");

	return employeeTLIO;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIO employeeTLIOExample6()
{
	if (containsObject(EmployeeTLIO.class, "0006")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIO) getObject(EmployeeTLIO.class, "0006");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIO employeeTLIO = new EmployeeTLIO();

	try {
		
	employeeTLIO.setFirstName("Marcus");
	employeeTLIO.setLastName("Saunders");
	employeeTLIO.setMale();
	employeeTLIO.setSalary(54300);
	employeeTLIO.setAddress(addressTLIOExample3());
	employeeTLIO.addPhoneNumber(phoneNumberTLIOExample6());
	employeeTLIO.addPhoneNumber(phoneNumberTLIOExample1());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeTLIO.class, employeeTLIO, "0006");

	return employeeTLIO;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIO employeeTLIOExample7()
{
	if (containsObject(EmployeeTLIO.class, "0007")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIO) getObject(EmployeeTLIO.class, "0007");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIO employeeTLIO = new EmployeeTLIO();

	try {
		
	employeeTLIO.setFirstName("Nancy");
	employeeTLIO.setLastName("White");
	employeeTLIO.setFemale();
	employeeTLIO.setSalary(31000);
	employeeTLIO.setAddress(addressTLIOExample7());
	employeeTLIO.addPhoneNumber(phoneNumberTLIOExample3());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeTLIO.class, employeeTLIO, "0007");

	return employeeTLIO;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIO employeeTLIOExample8()
{
	if (containsObject(EmployeeTLIO.class, "0008")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIO) getObject(EmployeeTLIO.class, "0008");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIO employeeTLIO = new EmployeeTLIO();

	try {
		
	employeeTLIO.setFirstName("Fred");
	employeeTLIO.setLastName("Jones");
	employeeTLIO.setMale();
	employeeTLIO.setSalary(500000);
	employeeTLIO.setAddress(addressTLIOExample8());
	employeeTLIO.addPhoneNumber(phoneNumberTLIOExample4());
	employeeTLIO.addPhoneNumber(phoneNumberTLIOExample6());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeTLIO.class, employeeTLIO, "0008");

	return employeeTLIO;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIO employeeTLIOExample9()
{
	if (containsObject(EmployeeTLIO.class, "0009")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIO) getObject(EmployeeTLIO.class, "0009");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeTLIO employeeTLIO = new EmployeeTLIO();

	try {
		
	employeeTLIO.setFirstName("Betty");
	employeeTLIO.setLastName("Jones");
	employeeTLIO.setFemale();
	employeeTLIO.setSalary(500001);
	employeeTLIO.setAddress(addressTLIOExample9());

	employeeTLIO.addPhoneNumber(phoneNumberTLIOExample1());
	employeeTLIO.addPhoneNumber(phoneNumberTLIOExample6());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeTLIO.class, employeeTLIO, "0009");

	return employeeTLIO;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIC employeeVLICExample1()
{
	if (containsObject(EmployeeVLIC.class, "0001")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIC) getObject(EmployeeVLIC.class, "0001");
	}
	
	org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIC employeeVLIC = new EmployeeVLIC();
	
	try {
		
	employeeVLIC.setFirstName("Bob");
	employeeVLIC.setLastName("Smith");
	employeeVLIC.setMale();
	employeeVLIC.setSalary(35000);	
	employeeVLIC.setAddress(addressVLICExample1());
	employeeVLIC.addPhoneNumber(phoneNumberVLICExample1());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};

	registerObject(EmployeeVLIC.class, employeeVLIC, "0001");

	return employeeVLIC;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIC employeeVLICExample10()
{
	if (containsObject(EmployeeVLIC.class, "0010")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIC) getObject(EmployeeVLIC.class, "0010");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIC employeeVLIC = new EmployeeVLIC();

	try {
		
	employeeVLIC.setFirstName("Jill");
	employeeVLIC.setLastName("May");
	employeeVLIC.setFemale();
	employeeVLIC.setAddress(addressVLICExample10());
	employeeVLIC.setSalary(56232);
	employeeVLIC.addPhoneNumber(phoneNumberVLICExample1());
	employeeVLIC.addPhoneNumber(phoneNumberVLICExample2());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeVLIC.class, employeeVLIC, "0010");

	return employeeVLIC;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIC employeeVLICExample11()
{
	if (containsObject(EmployeeVLIC.class, "0011")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIC) getObject(EmployeeVLIC.class, "0011");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIC employeeVLIC = new EmployeeVLIC();

	try {
		
	employeeVLIC.setFirstName("Sarah-loo");
	employeeVLIC.setLastName("Smitty");
	employeeVLIC.setFemale();
	employeeVLIC.setAddress(addressVLICExample11());
	employeeVLIC.setSalary(75000);
	employeeVLIC.addPhoneNumber(phoneNumberVLICExample2());
	employeeVLIC.addPhoneNumber(phoneNumberVLICExample3());
	employeeVLIC.addPhoneNumber(phoneNumberVLICExample4());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeVLIC.class, employeeVLIC, "0011");

	return employeeVLIC;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIC employeeVLICExample12()
{
	if (containsObject(EmployeeVLIC.class, "0012")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIC) getObject(EmployeeVLIC.class, "0012");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIC employeeVLIC = new EmployeeVLIC();

	try {
		
	employeeVLIC.setFirstName("Jim-bob");
	employeeVLIC.setLastName("Jefferson");
	employeeVLIC.setMale();
	employeeVLIC.setAddress(addressVLICExample12());
	employeeVLIC.setSalary(50000);
	employeeVLIC.addPhoneNumber(phoneNumberVLICExample3());
	employeeVLIC.addPhoneNumber(phoneNumberVLICExample4());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeVLIC.class, employeeVLIC, "0012");

	return employeeVLIC;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIC employeeVLICExample2()
{
	if (containsObject(EmployeeVLIC.class, "0002")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIC) getObject(EmployeeVLIC.class, "0002");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIC employeeVLIC = new EmployeeVLIC();

	try {
		
	employeeVLIC.setFirstName("John");
	employeeVLIC.setLastName("Way");
	employeeVLIC.setMale();
	employeeVLIC.setSalary(53000);
	employeeVLIC.setAddress(addressVLICExample2());
	employeeVLIC.addPhoneNumber(phoneNumberVLICExample1());
	employeeVLIC.addPhoneNumber(phoneNumberVLICExample6());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeVLIC.class, employeeVLIC, "0002");

	return employeeVLIC;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIC employeeVLICExample3()
{
	if (containsObject(EmployeeVLIC.class, "0003")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIC) getObject(EmployeeVLIC.class, "0003");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIC employeeVLIC = new EmployeeVLIC();

	try {
		
	employeeVLIC.setFirstName("Charles");
	employeeVLIC.setLastName("Chanley");
	employeeVLIC.setMale();
	employeeVLIC.setSalary(43000);
	employeeVLIC.setAddress(addressVLICExample6());
	employeeVLIC.addPhoneNumber(phoneNumberVLICExample5());
	employeeVLIC.addPhoneNumber(phoneNumberVLICExample6());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeVLIC.class, employeeVLIC, "0003");

	return employeeVLIC;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIC employeeVLICExample4()
{
	if (containsObject(EmployeeVLIC.class, "0004")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIC) getObject(EmployeeVLIC.class, "0004");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIC employeeVLIC = new EmployeeVLIC();

	try {
		
	employeeVLIC.setFirstName("Emanual");
	employeeVLIC.setLastName("Smith");
	employeeVLIC.setMale();
	employeeVLIC.setSalary(49631);
	employeeVLIC.setAddress(addressVLICExample5());
	employeeVLIC.addPhoneNumber(phoneNumberVLICExample2());
	employeeVLIC.addPhoneNumber(phoneNumberVLICExample4());
	employeeVLIC.addPhoneNumber(phoneNumberVLICExample5());
	employeeVLIC.addPhoneNumber(phoneNumberVLICExample6());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeVLIC.class, employeeVLIC, "0004");

	return employeeVLIC;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIC employeeVLICExample5()
{
	if (containsObject(EmployeeVLIC.class, "0005")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIC) getObject(EmployeeVLIC.class, "0005");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIC employeeVLIC = new EmployeeVLIC();

	try {
		
	employeeVLIC.setFirstName("Sarah");
	employeeVLIC.setLastName("Way");
	employeeVLIC.setFemale();
	employeeVLIC.setSalary(87000);
	employeeVLIC.setAddress(addressVLICExample4());
	employeeVLIC.addPhoneNumber(phoneNumberVLICExample1());
	employeeVLIC.addPhoneNumber(phoneNumberVLICExample6());
	employeeVLIC.addPhoneNumber(phoneNumberVLICExample3());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeVLIC.class, employeeVLIC, "0005");

	return employeeVLIC;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIC employeeVLICExample6()
{
	if (containsObject(EmployeeVLIC.class, "0006")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIC) getObject(EmployeeVLIC.class, "0006");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIC employeeVLIC = new EmployeeVLIC();

	try {
		
	employeeVLIC.setFirstName("Marcus");
	employeeVLIC.setLastName("Saunders");
	employeeVLIC.setMale();
	employeeVLIC.setSalary(54300);
	employeeVLIC.setAddress(addressVLICExample3());
	employeeVLIC.addPhoneNumber(phoneNumberVLICExample6());
	employeeVLIC.addPhoneNumber(phoneNumberVLICExample1());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeVLIC.class, employeeVLIC, "0006");

	return employeeVLIC;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIC employeeVLICExample7()
{
	if (containsObject(EmployeeVLIC.class, "0007")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIC) getObject(EmployeeVLIC.class, "0007");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIC employeeVLIC = new EmployeeVLIC();

	try {
		
	employeeVLIC.setFirstName("Nancy");
	employeeVLIC.setLastName("White");
	employeeVLIC.setFemale();
	employeeVLIC.setSalary(31000);
	employeeVLIC.setAddress(addressVLICExample7());
	employeeVLIC.addPhoneNumber(phoneNumberVLICExample3());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeVLIC.class, employeeVLIC, "0007");

	return employeeVLIC;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIC employeeVLICExample8()
{
	if (containsObject(EmployeeVLIC.class, "0008")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIC) getObject(EmployeeVLIC.class, "0008");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIC employeeVLIC = new EmployeeVLIC();

	try {
		
	employeeVLIC.setFirstName("Fred");
	employeeVLIC.setLastName("Jones");
	employeeVLIC.setMale();
	employeeVLIC.setSalary(500000);
	employeeVLIC.setAddress(addressVLICExample8());
	employeeVLIC.addPhoneNumber(phoneNumberVLICExample4());
	employeeVLIC.addPhoneNumber(phoneNumberVLICExample6());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeVLIC.class, employeeVLIC, "0008");

	return employeeVLIC;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIC employeeVLICExample9()
{
	if (containsObject(EmployeeVLIC.class, "0009")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIC) getObject(EmployeeVLIC.class, "0009");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIC employeeVLIC = new EmployeeVLIC();

	try {
		
	employeeVLIC.setFirstName("Betty");
	employeeVLIC.setLastName("Jones");
	employeeVLIC.setFemale();
	employeeVLIC.setSalary(500001);
	employeeVLIC.setAddress(addressVLICExample9());

	employeeVLIC.addPhoneNumber(phoneNumberVLICExample1());
	employeeVLIC.addPhoneNumber(phoneNumberVLICExample6());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeVLIC.class, employeeVLIC, "0009");

	return employeeVLIC;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIO employeeVLIOExample1()
{
	if (containsObject(EmployeeVLIO.class, "0001")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIO) getObject(EmployeeVLIO.class, "0001");
	}
	
	org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIO employeeVLIO = new EmployeeVLIO();
	
	try {
		
	employeeVLIO.setFirstName("Bob");
	employeeVLIO.setLastName("Smith");
	employeeVLIO.setMale();
	employeeVLIO.setSalary(35000);	
	employeeVLIO.setAddress(addressVLIOExample1());
	employeeVLIO.addPhoneNumber(phoneNumberVLIOExample1());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};

	registerObject(EmployeeVLIO.class, employeeVLIO, "0001");

	return employeeVLIO;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIO employeeVLIOExample10()
{
	if (containsObject(EmployeeVLIO.class, "0010")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIO) getObject(EmployeeVLIO.class, "0010");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIO employeeVLIO = new EmployeeVLIO();

	try {
		
	employeeVLIO.setFirstName("Jill");
	employeeVLIO.setLastName("May");
	employeeVLIO.setFemale();
	employeeVLIO.setAddress(addressVLIOExample10());
	employeeVLIO.setSalary(56232);
	employeeVLIO.addPhoneNumber(phoneNumberVLIOExample1());
	employeeVLIO.addPhoneNumber(phoneNumberVLIOExample2());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeVLIO.class, employeeVLIO, "0010");

	return employeeVLIO;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIO employeeVLIOExample11()
{
	if (containsObject(EmployeeVLIO.class, "0011")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIO) getObject(EmployeeVLIO.class, "0011");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIO employeeVLIO = new EmployeeVLIO();

	try {
		
	employeeVLIO.setFirstName("Sarah-loo");
	employeeVLIO.setLastName("Smitty");
	employeeVLIO.setFemale();
	employeeVLIO.setAddress(addressVLIOExample11());
	employeeVLIO.setSalary(75000);
	employeeVLIO.addPhoneNumber(phoneNumberVLIOExample2());
	employeeVLIO.addPhoneNumber(phoneNumberVLIOExample3());
	employeeVLIO.addPhoneNumber(phoneNumberVLIOExample4());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeVLIO.class, employeeVLIO, "0011");

	return employeeVLIO;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIO employeeVLIOExample12()
{
	if (containsObject(EmployeeVLIO.class, "0012")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIO) getObject(EmployeeVLIO.class, "0012");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIO employeeVLIO = new EmployeeVLIO();

	try {
		
	employeeVLIO.setFirstName("Jim-bob");
	employeeVLIO.setLastName("Jefferson");
	employeeVLIO.setMale();
	employeeVLIO.setAddress(addressVLIOExample12());
	employeeVLIO.setSalary(50000);
	employeeVLIO.addPhoneNumber(phoneNumberVLIOExample3());
	employeeVLIO.addPhoneNumber(phoneNumberVLIOExample4());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeVLIO.class, employeeVLIO, "0012");

	return employeeVLIO;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIO employeeVLIOExample2()
{
	if (containsObject(EmployeeVLIO.class, "0002")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIO) getObject(EmployeeVLIO.class, "0002");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIO employeeVLIO = new EmployeeVLIO();

	try {
		
	employeeVLIO.setFirstName("John");
	employeeVLIO.setLastName("Way");
	employeeVLIO.setMale();
	employeeVLIO.setSalary(53000);
	employeeVLIO.setAddress(addressVLIOExample2());
	employeeVLIO.addPhoneNumber(phoneNumberVLIOExample1());
	employeeVLIO.addPhoneNumber(phoneNumberVLIOExample6());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeVLIO.class, employeeVLIO, "0002");

	return employeeVLIO;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIO employeeVLIOExample3()
{
	if (containsObject(EmployeeVLIO.class, "0003")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIO) getObject(EmployeeVLIO.class, "0003");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIO employeeVLIO = new EmployeeVLIO();

	try {
		
	employeeVLIO.setFirstName("Charles");
	employeeVLIO.setLastName("Chanley");
	employeeVLIO.setMale();
	employeeVLIO.setSalary(43000);
	employeeVLIO.setAddress(addressVLIOExample6());
	employeeVLIO.addPhoneNumber(phoneNumberVLIOExample5());
	employeeVLIO.addPhoneNumber(phoneNumberVLIOExample6());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeVLIO.class, employeeVLIO, "0003");

	return employeeVLIO;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIO employeeVLIOExample4()
{
	if (containsObject(EmployeeVLIO.class, "0004")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIO) getObject(EmployeeVLIO.class, "0004");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIO employeeVLIO = new EmployeeVLIO();

	try {
		
	employeeVLIO.setFirstName("Emanual");
	employeeVLIO.setLastName("Smith");
	employeeVLIO.setMale();
	employeeVLIO.setSalary(49631);
	employeeVLIO.setAddress(addressVLIOExample5());
	employeeVLIO.addPhoneNumber(phoneNumberVLIOExample2());
	employeeVLIO.addPhoneNumber(phoneNumberVLIOExample4());
	employeeVLIO.addPhoneNumber(phoneNumberVLIOExample5());
	employeeVLIO.addPhoneNumber(phoneNumberVLIOExample6());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeVLIO.class, employeeVLIO, "0004");

	return employeeVLIO;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIO employeeVLIOExample5()
{
	if (containsObject(EmployeeVLIO.class, "0005")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIO) getObject(EmployeeVLIO.class, "0005");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIO employeeVLIO = new EmployeeVLIO();

	try {
		
	employeeVLIO.setFirstName("Sarah");
	employeeVLIO.setLastName("Way");
	employeeVLIO.setFemale();
	employeeVLIO.setSalary(87000);
	employeeVLIO.setAddress(addressVLIOExample4());
	employeeVLIO.addPhoneNumber(phoneNumberVLIOExample1());
	employeeVLIO.addPhoneNumber(phoneNumberVLIOExample6());
	employeeVLIO.addPhoneNumber(phoneNumberVLIOExample3());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeVLIO.class, employeeVLIO, "0005");

	return employeeVLIO;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIO employeeVLIOExample6()
{
	if (containsObject(EmployeeVLIO.class, "0006")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIO) getObject(EmployeeVLIO.class, "0006");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIO employeeVLIO = new EmployeeVLIO();

	try {
		
	employeeVLIO.setFirstName("Marcus");
	employeeVLIO.setLastName("Saunders");
	employeeVLIO.setMale();
	employeeVLIO.setSalary(54300);
	employeeVLIO.setAddress(addressVLIOExample3());
	employeeVLIO.addPhoneNumber(phoneNumberVLIOExample6());
	employeeVLIO.addPhoneNumber(phoneNumberVLIOExample1());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeVLIO.class, employeeVLIO, "0006");

	return employeeVLIO;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIO employeeVLIOExample7()
{
	if (containsObject(EmployeeVLIO.class, "0007")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIO) getObject(EmployeeVLIO.class, "0007");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIO employeeVLIO = new EmployeeVLIO();

	try {
		
	employeeVLIO.setFirstName("Nancy");
	employeeVLIO.setLastName("White");
	employeeVLIO.setFemale();
	employeeVLIO.setSalary(31000);
	employeeVLIO.setAddress(addressVLIOExample7());
	employeeVLIO.addPhoneNumber(phoneNumberVLIOExample3());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeVLIO.class, employeeVLIO, "0007");

	return employeeVLIO;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIO employeeVLIOExample8()
{
	if (containsObject(EmployeeVLIO.class, "0008")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIO) getObject(EmployeeVLIO.class, "0008");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIO employeeVLIO = new EmployeeVLIO();

	try {
		
	employeeVLIO.setFirstName("Fred");
	employeeVLIO.setLastName("Jones");
	employeeVLIO.setMale();
	employeeVLIO.setSalary(500000);
	employeeVLIO.setAddress(addressVLIOExample8());
	employeeVLIO.addPhoneNumber(phoneNumberVLIOExample4());
	employeeVLIO.addPhoneNumber(phoneNumberVLIOExample6());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeVLIO.class, employeeVLIO, "0008");

	return employeeVLIO;
}
public org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIO employeeVLIOExample9()
{
	if (containsObject(EmployeeVLIO.class, "0009")) {
		return (org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIO) getObject(EmployeeVLIO.class, "0009");
	}
		
	org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIO employeeVLIO = new EmployeeVLIO();

	try {
		
	employeeVLIO.setFirstName("Betty");
	employeeVLIO.setLastName("Jones");
	employeeVLIO.setFemale();
	employeeVLIO.setSalary(500001);
	employeeVLIO.setAddress(addressVLIOExample9());

	employeeVLIO.addPhoneNumber(phoneNumberVLIOExample1());
	employeeVLIO.addPhoneNumber(phoneNumberVLIOExample6());
	
	} catch (Exception exception) {
		throw new RuntimeException(exception.toString());
	};
	registerObject(EmployeeVLIO.class, employeeVLIO, "0009");

	return employeeVLIO;
}

protected Vector getAllObjects() 
{
	return populationManager.getAllObjects();
}
public Vector getAllObjectsForClass(Class domainClass)
{
	return populationManager.getAllObjectsForClass(domainClass);
}
protected Object getObject(Class domainClass, String identifier) 
{
	return populationManager.getObject(domainClass, identifier);
}
public PhoneNumberTLIC phoneNumberTLICExample1()
{
	return new PhoneNumberTLIC("Work", "613" , "2258812");
}
public PhoneNumberTLIC phoneNumberTLICExample2()
{
	return new PhoneNumberTLIC("Work Fax", "613" , "2255943");
}
public PhoneNumberTLIC phoneNumberTLICExample3()
{
	return new PhoneNumberTLIC("Home", "613" , "5551234");
}
public PhoneNumberTLIC phoneNumberTLICExample4()
{
	return new PhoneNumberTLIC("Cellular", "416" , "5551111");
}
public PhoneNumberTLIC phoneNumberTLICExample5()
{
	return new PhoneNumberTLIC("Pager", "976" , "5556666");
}
public PhoneNumberTLIC phoneNumberTLICExample6()
{
	return new PhoneNumberTLIC("ISDN", "905" , "5553691");
}
public PhoneNumberTLIO phoneNumberTLIOExample1()
{
	return new PhoneNumberTLIO("Work", "613" , "2258812");
}
public PhoneNumberTLIO phoneNumberTLIOExample2()
{
	return new PhoneNumberTLIO("Work Fax", "613" , "2255943");
}
public PhoneNumberTLIO phoneNumberTLIOExample3()
{
	return new PhoneNumberTLIO("Home", "613" , "5551234");
}
public PhoneNumberTLIO phoneNumberTLIOExample4()
{
	return new PhoneNumberTLIO("Cellular", "416" , "5551111");
}
public PhoneNumberTLIO phoneNumberTLIOExample5()
{
	return new PhoneNumberTLIO("Pager", "976" , "5556666");
}
public PhoneNumberTLIO phoneNumberTLIOExample6()
{
	return new PhoneNumberTLIO("ISDN", "905" , "5553691");
}
public PhoneNumberVLIC phoneNumberVLICExample1()
{
	return new PhoneNumberVLIC("Work", "613" , "2258812");
}
public PhoneNumberVLIC phoneNumberVLICExample2()
{
	return new PhoneNumberVLIC("Work Fax", "613" , "2255943");
}
public PhoneNumberVLIC phoneNumberVLICExample3()
{
	return new PhoneNumberVLIC("Home", "613" , "5551234");
}
public PhoneNumberVLIC phoneNumberVLICExample4()
{
	return new PhoneNumberVLIC("Cellular", "416" , "5551111");
}
public PhoneNumberVLIC phoneNumberVLICExample5()
{
	return new PhoneNumberVLIC("Pager", "976" , "5556666");
}
public PhoneNumberVLIC phoneNumberVLICExample6()
{
	return new PhoneNumberVLIC("ISDN", "905" , "5553691");
}
public PhoneNumberVLIO phoneNumberVLIOExample1()
{
	return new PhoneNumberVLIO("Work", "613" , "2258812");
}
public PhoneNumberVLIO phoneNumberVLIOExample2()
{
	return new PhoneNumberVLIO("Work Fax", "613" , "2255943");
}
public PhoneNumberVLIO phoneNumberVLIOExample3()
{
	return new PhoneNumberVLIO("Home", "613" , "5551234");
}
public PhoneNumberVLIO phoneNumberVLIOExample4()
{
	return new PhoneNumberVLIO("Cellular", "416" , "5551111");
}
public PhoneNumberVLIO phoneNumberVLIOExample5()
{
	return new PhoneNumberVLIO("Pager", "976" , "5556666");
}
public PhoneNumberVLIO phoneNumberVLIOExample6()
{
	return new PhoneNumberVLIO("ISDN", "905" , "5553691");
}

protected void registerObject(Class domainClass, Object domainObject, String identifier) 
{
	populationManager.registerObject(domainClass, domainObject, identifier);
}
protected void registerObject(Object domainObject, String identifier) 
{
	populationManager.registerObject(domainObject, identifier);
}
}
