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
