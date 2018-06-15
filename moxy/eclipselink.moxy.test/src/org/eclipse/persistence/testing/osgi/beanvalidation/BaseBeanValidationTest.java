/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Dmitry Kornilov - initial implementation
package org.eclipse.persistence.testing.osgi.beanvalidation;

/**
 * Base class for OSGi bean validation tests. Contains test data.
 *
 * @author Dmitry Kornilov
 * @since 2.7.0
 */
public class BaseBeanValidationTest {
    static final String CUSTOMER_VALID_XML = "<customer id=\"1\"><name>John Doe</name></customer>";
    static final String CUSTOMER_INVALID_XML = "<customer><name>X</name></customer>";

    Customer createInvalidCustomer() {
        Customer customer = new Customer();
        customer.setId(null);
        customer.setName("X");
        return customer;
    }

    Customer createValidCustomer() {
        Customer customer = new Customer();
        customer.setId("1");
        customer.setName("John Doe");
        return customer;
    }
}
