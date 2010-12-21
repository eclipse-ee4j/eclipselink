/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Oracle = 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmltransformation;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class XmlTransformationTestCases extends JAXBTestCases {
    public XmlTransformationTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {Employee.class});
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/transformation/employee1.xml");
    }
    
    public Object getControlObject() {
        Employee emp = new Employee();
        emp.name = "John Smith";
        String[] hours = new String[2];
        hours[0] = "9:00AM";
        hours[1] = "5:00PM";
        emp.normalHours = hours;
        return emp;
    }
}
