/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Praba Vijayaratnam - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxrs.utils;

import org.eclipse.persistence.testing.jaxrs.model.Address;
import org.eclipse.persistence.testing.jaxrs.model.Customer;
import org.eclipse.persistence.testing.jaxrs.model.PhoneNumber;

import java.util.ArrayList;
import java.util.List;

public class JAXRSExamples {
    public static Address addressExample(int id, String street, String city) {
        Address address = new Address();
        address.setId(id);
        address.setStreet(street);
        address.setCity(city);
        return address;
    }

    public static PhoneNumber phoneNumberExample(int id, String num, String type, Customer customer) {
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setId(id);
        phoneNumber.setNum(num);
        phoneNumber.setType(type);
        phoneNumber.setCustomer(customer);
        return phoneNumber;
    }

    public static Customer customerExample1() {
        Customer customer = new Customer();
        customer.setId(1);
        customer.setFirstName("Jane");
        customer.setLastName("Doe");
        customer.setAddress(addressExample(1, "1 A Street", "Ottawa"));

        List<PhoneNumber> phoneNumbers = new ArrayList<>(2);
        phoneNumbers.add(phoneNumberExample(1, "555-1111", "WORK", customer));
        phoneNumbers.add(phoneNumberExample(2, "555-2222", "HOME", customer));
        customer.setPhoneNumbers(phoneNumbers);

        return customer;
    }

    public static Customer customerExample2() {
        Customer customer = new Customer();
        customer.setId(2);
        customer.setFirstName("Jill");
        customer.setLastName("May");
        customer.setAddress(addressExample(2, "1111 Moose Rd.", "Calgary"));

        List<PhoneNumber> phoneNumbers = new ArrayList<>(2);
        phoneNumbers.add(phoneNumberExample(3, "555-3333", "WORK", customer));
        phoneNumbers.add(phoneNumberExample(4, "555-4444", "HOME", customer));
        customer.setPhoneNumbers(phoneNumbers);

        return customer;
    }

    public static Customer customerExample3() {
        Customer customer = new Customer();

        customer.setId(3);
        customer.setFirstName("Sarah");
        customer.setLastName("Smith");
        customer.setAddress(addressExample(3, "1 Nowhere Drive", "Ottawa"));

        List<PhoneNumber> phoneNumbers = new ArrayList<>(2);
        phoneNumbers.add(phoneNumberExample(5, "555-5555", "WORK", customer));
        phoneNumbers.add(phoneNumberExample(6, "555-6666", "HOME", customer));
        customer.setPhoneNumbers(phoneNumbers);

        return customer;
    }

    public static Customer customerExample4() {
        Customer customer = new Customer();

        customer.setId(4);
        customer.setFirstName("John");
        customer.setLastName("Does");
        customer.setAddress(addressExample(4, "4 A Street", "AnyTown"));

        List<PhoneNumber> phoneNumbers = new ArrayList<>(2);
        phoneNumbers.add(phoneNumberExample(7, "555-7777", "WORK", customer));
        phoneNumbers.add(phoneNumberExample(8, "555-8888", "HOME", customer));
        customer.setPhoneNumbers(phoneNumbers);

        return customer;
    }

    public static Customer customerExample5() {
        Customer customer = new Customer();

        customer.setId(5);
        customer.setFirstName("Jack");
        customer.setLastName("Daniel");
        customer.setAddress(addressExample(5, "5 B Street", "YourTown"));

        List<PhoneNumber> phoneNumbers = new ArrayList<>(2);
        phoneNumbers.add(phoneNumberExample(9, "555-9999", "WORK", customer));
        phoneNumbers.add(phoneNumberExample(10, "555-1010", "HOME", customer));
        customer.setPhoneNumbers(phoneNumbers);

        return customer;
    }

    public static Customer customerExample6() {
        Customer customer = new Customer();

        customer.setId(6);
        customer.setFirstName("Sera");
        customer.setLastName("Quesera");
        customer.setAddress(addressExample(6, "101 espanol route", "Barcelona"));

        List<PhoneNumber> phoneNumbers = new ArrayList<>(2);
        phoneNumbers.add(phoneNumberExample(11, "555-1111", "WORK", customer));
        phoneNumbers.add(phoneNumberExample(12, "555-1212", "HOME", customer));
        customer.setPhoneNumbers(phoneNumbers);

        return customer;
    }

    public static Customer customerExample8() {
        Customer customer = new Customer();

        customer.setId(8);
        customer.setFirstName("Jean");
        customer.setLastName("Daisy");
        customer.setAddress(addressExample(8, "8 A Street", "AnyTown"));

        List<PhoneNumber> phoneNumbers = new ArrayList<>(2);
        phoneNumbers.add(phoneNumberExample(15, "555-1515", "WORK", customer));
        phoneNumbers.add(phoneNumberExample(16, "555-1616", "HOME", customer));
        customer.setPhoneNumbers(phoneNumbers);

        return customer;
    }

    public static Customer customerExample9() {
        Customer customer = new Customer();

        customer.setId(9);
        customer.setFirstName("John");
        customer.setLastName("Day");
        customer.setAddress(addressExample(9, "9 Route", "ManyTown"));

        List<PhoneNumber> phoneNumbers = new ArrayList<>(2);
        phoneNumbers.add(phoneNumberExample(17, "555-1717", "WORK", customer));
        phoneNumbers.add(phoneNumberExample(18, "555-1818", "HOME", customer));
        customer.setPhoneNumbers(phoneNumbers);

        return customer;
    }

    public static Customer customerExample10() {
        Customer customer = new Customer();

        customer.setId(10);
        customer.setFirstName("Adams");
        customer.setLastName("Braves");
        customer.setAddress(addressExample(10, "1 A Street", "Kanata"));

        List<PhoneNumber> phoneNumbers = new ArrayList<>(2);
        phoneNumbers.add(phoneNumberExample(19, "555-1919", "WORK", customer));
        phoneNumbers.add(phoneNumberExample(20, "555-2020", "HOME", customer));
        customer.setPhoneNumbers(phoneNumbers);

        return customer;
    }

    public static Customer customerExample11() {
        Customer customer = new Customer();

        customer.setId(11);
        customer.setFirstName("Adams");
        customer.setLastName("Eve");
        customer.setAddress(addressExample(11, "11 Nowhere Drive", "Orleans"));

        List<PhoneNumber> phoneNumbers = new ArrayList<>(2);
        phoneNumbers.add(phoneNumberExample(21, "555-2121", "WORK", customer));
        phoneNumbers.add(phoneNumberExample(22, "555-2222", "HOME", customer));
        customer.setPhoneNumbers(phoneNumbers);

        return customer;
    }

    public static Customer customerExample12() {
        Customer customer = new Customer();

        customer.setId(12);
        customer.setFirstName("Adams");
        customer.setLastName("Family");
        customer.setAddress(addressExample(12, "12th Avenue", "Barhaven"));

        List<PhoneNumber> phoneNumbers = new ArrayList<>(2);
        phoneNumbers.add(phoneNumberExample(23, "555-2323", "WORK", customer));
        phoneNumbers.add(phoneNumberExample(24, "555-2424", "HOME", customer));
        customer.setPhoneNumbers(phoneNumbers);

        return customer;
    }

    public static Customer customerExample13() {
        Customer customer = new Customer();

        customer.setId(13);
        customer.setFirstName("Larry");
        customer.setLastName("Robinson");
        customer.setAddress(addressExample(13, "1 Querbes Avenue", "Montreal"));

        List<PhoneNumber> phoneNumbers = new ArrayList<>(2);
        phoneNumbers.add(phoneNumberExample(25, "555-2525", "WORK", customer));
        phoneNumbers.add(phoneNumberExample(26, "555-2626", "HOME", customer));
        customer.setPhoneNumbers(phoneNumbers);

        return customer;
    }

    public static Customer customerExample14() {
        Customer customer = new Customer();

        customer.setId(14);
        customer.setFirstName("Brian");
        customer.setLastName("Bellows");
        customer.setAddress(addressExample(14, "14th Avenue", "Barhaven"));

        List<PhoneNumber> phoneNumbers = new ArrayList<>(2);
        phoneNumbers.add(phoneNumberExample(27, "555-2727", "WORK", customer));
        phoneNumbers.add(phoneNumberExample(28, "555-2828", "HOME", customer));
        customer.setPhoneNumbers(phoneNumbers);

        return customer;
    }

    public static Customer customerExample15() {
        Customer customer = new Customer();

        customer.setId(15);
        customer.setFirstName("Bob");
        customer.setLastName("Gainey");
        customer.setAddress(addressExample(15, "15th Avenue", "Montreal"));

        List<PhoneNumber> phoneNumbers = new ArrayList<>(2);
        phoneNumbers.add(phoneNumberExample(29, "555-2929", "WORK", customer));
        phoneNumbers.add(phoneNumberExample(30, "555-3030", "HOME", customer));
        customer.setPhoneNumbers(phoneNumbers);

        return customer;
    }
}
