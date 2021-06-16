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
//     mmacivor - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlwriteonly;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlwriteonly.Employee;

public class XmlWriteOnlyTestCases extends JAXBWithJSONTestCases{

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlwriteonly/employee.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlwriteonly/employee.json";

    public XmlWriteOnlyTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {Employee.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    public Object getControlObject() {
        return getWriteControlObject();
    }

    @Override
    public Object getWriteControlObject() {
        Employee emp = new Employee();
        emp.name = "Jane Doe";
        emp.writeOnlyField = "Write Only Data";
        return emp;
    }

    @Override
    public Object getReadControlObject() {
        Employee emp = new Employee();
        emp.name = "Jane Doe";
        emp.writeOnlyField = null;
        return emp;
    }

    @Override
    public void testRoundTrip() {
        //Not Applicable due to use of separate read and write docs
    }
}

