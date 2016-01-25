/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.persistence.json.bind.defaultmapping.specific;

import org.eclipse.persistence.json.bind.defaultmapping.specific.model.Address;
import org.eclipse.persistence.json.bind.defaultmapping.specific.model.Customer;
import org.eclipse.persistence.json.bind.defaultmapping.specific.model.Street;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author Roman Grigoriadi
 */
public abstract class CustomerTest {

    protected void assertCustomerValues(Customer customer, String customerName) {
        assertEquals(customerName, customer.getName());
        assertEquals(Integer.valueOf(33), customer.getAge());
        assertEquals(1, customer.getAddresses().size());
        for (Address address : customer.getAddresses()) {
            assertEquals("Prague", address.getTown());
            assertEquals("Zoubkova", address.getStreet().getName());
            assertEquals(Integer.valueOf(111), address.getStreet().getNumber());
        }

        if (customer.getStrings() != null) {
            assertEquals(2, customer.getStrings().size());
            assertEquals("green", customer.getStrings().get(0));
            assertEquals("yellow", customer.getStrings().get(1));
        }

        if (customer.getIntegers() != null) {
            assertEquals(2, customer.getIntegers().size());
            assertEquals(Integer.valueOf(0),  customer.getIntegers().get(0));
            assertEquals(Integer.valueOf(1), customer.getIntegers().get(1));
        }

        assertEquals(2, customer.getStringIntegerMap().size());
        assertEquals(Integer.valueOf(1), customer.getStringIntegerMap().get("first"));
        assertEquals(Integer.valueOf(2), customer.getStringIntegerMap().get("second"));
    }

    protected Customer createCustomer(String customerName) {
        Street street = new Street("Zoubkova", 111);
        Address address = new Address(street, "Prague");
        Customer customer = new Customer(33, customerName);

        List<Address> addresses = new ArrayList<>();
        addresses.add(address);
        customer.setAddresses(addresses);

        List<String> strings = new ArrayList<>();
        strings.add("green");
        strings.add("yellow");
        customer.setStrings(strings);

        List<Integer> integers = new ArrayList<>();
        integers.add(0);
        integers.add(1);
        customer.setIntegers(integers);

        Map<String, Integer> stringIntegerMap = new HashMap<>();
        stringIntegerMap.put("first", 1);
        stringIntegerMap.put("second", 2);
        customer.setStringIntegerMap(stringIntegerMap);

        List<List<Integer>> listOfListsOfIntegers = new ArrayList<>();
        for(int i=0; i<3; i++) {
            List<Integer> integerList = new ArrayList<>();
            integerList.add(0);
            integerList.add(1);
            integerList.add(2);
            listOfListsOfIntegers.add(integerList);
        }

        customer.setListOfListsOfIntegers(listOfListsOfIntegers);

        return customer;
    }
}
