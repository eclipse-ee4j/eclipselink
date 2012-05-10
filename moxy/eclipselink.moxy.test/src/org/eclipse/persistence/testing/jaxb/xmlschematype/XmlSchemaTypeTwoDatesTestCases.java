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
package org.eclipse.persistence.testing.jaxb.xmlschematype;

import java.util.Calendar;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlSchemaTypeTwoDatesTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlschematype/employee_two_dates.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlschematype/employee_two_dates.json";

    public XmlSchemaTypeTwoDatesTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);  
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = EmployeeTwoDates.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        EmployeeTwoDates employee = new EmployeeTwoDates();
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2005,04,24);
      
        employee.startDate = cal;

        cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.HOUR_OF_DAY, 16);
        cal.set(Calendar.MINUTE, 06);
        cal.set(Calendar.SECOND, 53);
        cal.set(Calendar.MILLISECOND, 0);


        employee.startTime = cal;
        return employee;
    }
}
