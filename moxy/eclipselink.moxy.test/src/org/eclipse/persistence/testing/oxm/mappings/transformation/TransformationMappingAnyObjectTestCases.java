/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
