/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.json.bind.JsonBindingBuilder;
import org.eclipse.persistence.json.bind.defaultmapping.specific.model.Address;
import org.eclipse.persistence.json.bind.defaultmapping.specific.model.Customer;
import org.eclipse.persistence.json.bind.defaultmapping.specific.model.Street;
import org.junit.Test;

import javax.json.bind.Jsonb;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author Roman Grigoriadi
 */
public class ObjectGraphTest {

    private static final String EXPECTED = "{\"addresses\":[{\"street\":{\"name\":\"Zoubkova\",\"number\":111},\"town\":\"Prague\"}],\"age\":33,\"friends\":{\"firstFriend\":{\"addresses\":[{\"street\":{\"name\":\"Zoubkova\",\"number\":111},\"town\":\"Prague\"}],\"age\":33,\"integers\":[0,1],\"listOfListsOfIntegers\":[[0,1,2],[0,1,2],[0,1,2]],\"name\":\"Jasons first friend\",\"stringIntegerMap\":{\"first\":1,\"second\":2},\"strings\":[\"green\",\"yellow\"]},\"secondFriend\":{\"addresses\":[{\"street\":{\"name\":\"Zoubkova\",\"number\":111},\"town\":\"Prague\"}],\"age\":33,\"integers\":[0,1],\"listOfListsOfIntegers\":[[0,1,2],[0,1,2],[0,1,2]],\"name\":\"Jasons second friend\",\"stringIntegerMap\":{\"first\":1,\"second\":2},\"strings\":[\"green\",\"yellow\"]}},\"integers\":[0,1],\"listOfListsOfIntegers\":[[0,1,2],[0,1,2],[0,1,2]],\"name\":\"Root Jason Customer\",\"stringIntegerMap\":{\"first\":1,\"second\":2},\"strings\":[\"green\",\"yellow\"]}";

    @Test
    public void testObjectToJson() {
        Customer customer = createCustomer("Root Jason Customer");

        Map<String, Customer> friends = new HashMap<>();
        friends.put("firstFriend", createCustomer("Jasons first friend"));
        friends.put("secondFriend", createCustomer("Jasons second friend"));
        customer.setFriends(friends);

        Jsonb jsonb = new JsonBindingBuilder().build();
        assertEquals(EXPECTED, jsonb.toJson(customer));
    }

    @Test
    public void testObjectFromJson() {
        Jsonb jsonb = new JsonBindingBuilder().build();
        Customer customer = jsonb.fromJson(EXPECTED, Customer.class);
        assertCustomerValues(customer, "Root Jason Customer");
        assertEquals(2, customer.getFriends().size());
        assertCustomerValues(customer.getFriends().get("firstFriend"), "Jasons first friend");
        assertCustomerValues(customer.getFriends().get("secondFriend"), "Jasons second friend");
    }

    private void assertCustomerValues(Customer customer, String customerName) {
        assertEquals(customerName, customer.getName());
        assertEquals(Integer.valueOf(33), customer.getAge());
        assertEquals(1, customer.getAddresses().size());
        for (Address address : customer.getAddresses()) {
            assertEquals("Prague", address.getTown());
            assertEquals("Zoubkova", address.getStreet().getName());
            assertEquals(Integer.valueOf(111), address.getStreet().getNumber());
        }

        assertEquals(2, customer.getStrings().size());
        assertEquals("green", customer.getStrings().get(0));
        assertEquals("yellow", customer.getStrings().get(1));

        assertEquals(2, customer.getIntegers().size());
        assertEquals(Integer.valueOf(0),  customer.getIntegers().get(0));
        assertEquals(Integer.valueOf(1), customer.getIntegers().get(1));

        assertEquals(2, customer.getStringIntegerMap().size());
        assertEquals(Integer.valueOf(1), customer.getStringIntegerMap().get("first"));
        assertEquals(Integer.valueOf(2), customer.getStringIntegerMap().get("second"));
    }

    private Customer createCustomer(String customerName) {
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
