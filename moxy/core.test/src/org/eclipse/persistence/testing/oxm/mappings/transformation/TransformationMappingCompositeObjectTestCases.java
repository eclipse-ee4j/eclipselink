/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.transformation;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class TransformationMappingCompositeObjectTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/transformation/RootWithCompositeObject.xml";
    private final static String CONTROL_EMPLOYEE_NAME = "John Smith";
    private final static String CONTROL_EMPLOYEE_START_TIME = "9:00AM";
    private final static String CONTROL_EMPLOYEE_END_TIME = "5:00PM";

    public TransformationMappingCompositeObjectTestCases(String name) throws Exception {
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

        RootWithCompositeObject rootWithCompositeObject = new RootWithCompositeObject();
        rootWithCompositeObject.employee = employee;

        return rootWithCompositeObject;
    }
}