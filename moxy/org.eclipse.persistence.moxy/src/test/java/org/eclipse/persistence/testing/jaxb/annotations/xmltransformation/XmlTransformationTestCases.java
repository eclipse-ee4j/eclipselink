/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
// Oracle = 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmltransformation;

import java.util.HashMap;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlTransformationTestCases extends JAXBWithJSONTestCases {
    public XmlTransformationTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {Employee.class});
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/transformation/employee1.xml");
        setControlJSON("org/eclipse/persistence/testing/oxm/mappings/transformation/employee1.json");
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
