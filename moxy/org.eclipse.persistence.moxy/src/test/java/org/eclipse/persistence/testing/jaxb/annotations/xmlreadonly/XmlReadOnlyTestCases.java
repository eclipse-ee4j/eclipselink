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
package org.eclipse.persistence.testing.jaxb.annotations.xmlreadonly;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlReadOnlyTestCases extends JAXBWithJSONTestCases {

    private static final String XML_READ_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlreadonly/employee_read.xml";
    private static final String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlreadonly/employee_write.xml";

    private static final String JSON_READ_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlreadonly/employee_read.json";
    private static final String JSON_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlreadonly/employee_write.json";
    public XmlReadOnlyTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {Employee.class});
        setControlDocument(XML_READ_RESOURCE);
        setWriteControlDocument(XML_WRITE_RESOURCE);
        setControlJSON(JSON_READ_RESOURCE);
        setWriteControlJSON(JSON_WRITE_RESOURCE);
    }

    public Object getControlObject() {
        Employee emp = new Employee();
        emp.name = "Jane Doe";
        emp.readOnlyField = "Read Only Data";
        return emp;
    }

    @Override
    public void testRoundTrip() {
        //Not Applicable due to use of separate read and write docs
    }
}

