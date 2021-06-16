/*
 * Copyright (c) 2015, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
