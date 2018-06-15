/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.sdo.helper.xmlhelper;

import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class SDOXMLHelperTestCases extends SDOTestCase {
    protected static final String CONTROL_ROOT_NAME = "customer";
    protected static final String CONTROL_ROOT_TYPE = "customer-type";
    protected static final String CONTROL_ROOT_URI = "http://www.example.org";
    protected static final String CONTROL_FIRST_NAME = "Jane";
    protected static final String CONTROL_LAST_NAME = "Smith";
    protected static final Integer CONTROL_CUSTOMERID = new Integer("111");
    protected static final String CONTROL_SIN = "123 456 789";
    protected static final String SIN = "sin";
    protected static final String CUSTOMERID = "customerID";
    protected static final String FIRSTNAME = "firstName";
    protected static final String LASTNAME = "lastName";

    public SDOXMLHelperTestCases(String name) {
        super(name);
    }
}
