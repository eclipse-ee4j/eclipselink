/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self;

import java.util.Calendar;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class CompositeObjectTwoSelfMappingsTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositeobject/self/CompositeObjectTwoSelfMappings.xml";
    private final static int CONTROL_START_YYYY = 1970;
    private final static int CONTROL_START_MM = 0;
    private final static int CONTROL_START_DD = 1;
    private final static int CONTROL_END_YYYY = 2004;
    private final static int CONTROL_END_MM = 0;
    private final static int CONTROL_END_DD = 1;
    private final static String CONTROL_STREET = "123 A St.";
    private final static String CONTROL_CITY = "Any Town";

    public CompositeObjectTwoSelfMappingsTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setProject(new TwoSelfMappingsProject());
    }

    protected Object getControlObject() {
        Calendar start = Calendar.getInstance();
        start.clear();
        start.set(CONTROL_START_YYYY, CONTROL_START_MM, CONTROL_START_DD);

        Calendar end = Calendar.getInstance();
        end.clear();
        end.set(CONTROL_END_YYYY, CONTROL_END_MM, CONTROL_END_DD);

        Employee employee = new Employee();

        Employee.Period period = employee.getStartDateAndEndDate();
        period.setStartDate(start);
        period.setEndDate(end);
        employee.setStartDateAndEndDate(period);
        
        Address address = new Address();
        address.street = CONTROL_STREET;
        address.city = CONTROL_CITY;
        employee.address = address;

        return employee;
    }
}
