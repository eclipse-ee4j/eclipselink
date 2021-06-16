/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.transformation;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class TransformationMappingAnyObjectTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/transformation/RootWithAnyObject.xml";
    private final static String CONTROL_EMPLOYEE_NAME = "John Smith";
    private final static String CONTROL_EMPLOYEE_START_TIME = "9:00AM";
    private final static String CONTROL_EMPLOYEE_END_TIME = "5:00PM";

    public TransformationMappingAnyObjectTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setProject(new TransformationMappingTestProject());
    }

    public Object getControlObject() {
        Employee employee = new Employee();
        employee.setName(CONTROL_EMPLOYEE_NAME);
        String[] hours = new String[2];
        hours[0] = CONTROL_EMPLOYEE_START_TIME;
        hours[1] = CONTROL_EMPLOYEE_END_TIME;
        employee.setNormalHours(hours);

        RootWithAnyObject rootWithAnyObject = new RootWithAnyObject();
        rootWithAnyObject.object = employee;

        return rootWithAnyObject;
    }
}
