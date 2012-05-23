/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Praba Vijayaratnam - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.jaxrs;

import java.util.*;

public class JAXRSExamples {

	public static Address addressExample1() {
		Address address = new Address();
		address.setId(1);
		address.setStreet("1 A Street");
		address.setCity("Ottawa");
		return address;
	}

	public static Address addressExample2() {
		Address address = new Address();
		address.setId(2);
		address.setStreet("1111 Moose Rd.");
		address.setCity("Calgary");
		return address;
	}

	public static Address addressExample3() {
		Address address = new Address();
		address.setId(3);
		address.setStreet("1 Nowhere Drive");
		address.setCity("Ottawa");
		return address;
	}

	public static Customer customerExample1() {
		Customer customer = new Customer();
		try {
			customer.setId(1);
			customer.setFirstName("Jane");
			customer.setLastName("Doe");
			customer.setAddress(addressExample1());

			List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>(2);
			PhoneNumber workPhone = new PhoneNumber();
			workPhone.setId(1);
			workPhone.setNum("555-1111");
			workPhone.setType("WORK");
			workPhone.setCustomer(customer);
			phoneNumbers.add(workPhone);

			PhoneNumber homePhone = new PhoneNumber();
			homePhone.setId(2);
			homePhone.setNum("555-2222");
			homePhone.setType("HOME");
			homePhone.setCustomer(customer);
			phoneNumbers.add(homePhone);

			customer.setPhoneNumbers(phoneNumbers);
		} catch (Exception exception) {
			throw new RuntimeException(exception.toString());
		}

		return customer;
	}

	public static Customer customerExample2() {
		Customer customer = new Customer();

		try {
			customer.setId(2);
			customer.setFirstName("Jill");
			customer.setLastName("May");
			customer.setAddress(addressExample2());

			List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>(2);
			PhoneNumber workPhone = new PhoneNumber();
			workPhone.setId(3);
			workPhone.setNum("555-3333");
			workPhone.setType("WORK");
			workPhone.setCustomer(customer);
			phoneNumbers.add(workPhone);
			PhoneNumber homePhone = new PhoneNumber();
			homePhone.setId(4);
			homePhone.setNum("555-4444");
			homePhone.setType("HOME");
			homePhone.setCustomer(customer);
			phoneNumbers.add(homePhone);

			customer.setPhoneNumbers(phoneNumbers);
		} catch (Exception exception) {
			throw new RuntimeException(exception.toString());
		}

		return customer;
	}

	public static Customer customerExample3() {
		Customer customer = new Customer();

		try {
			customer.setId(3);
			customer.setFirstName("Sarah-loo");
			customer.setLastName("Smitty");
			customer.setAddress(addressExample3());

			List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>(2);
			PhoneNumber workPhone = new PhoneNumber();
			workPhone.setId(5);
			workPhone.setNum("555-5555");
			workPhone.setType("WORK");
			workPhone.setCustomer(customer);
			phoneNumbers.add(workPhone);
			PhoneNumber homePhone = new PhoneNumber();
			homePhone.setId(6);
			homePhone.setNum("555-6666");
			homePhone.setType("HOME");
			homePhone.setCustomer(customer);
			phoneNumbers.add(homePhone);

			customer.setPhoneNumbers(phoneNumbers);
		} catch (Exception exception) {
			throw new RuntimeException(exception.toString());
		}

		return customer;
	}

}
