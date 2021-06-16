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
// Denise Smith - 2.3
package org.eclipse.persistence.testing.jaxb.annotations.xmltransformation;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XMLTransformationNoArgCtorTestCases extends JAXBWithJSONTestCases{
    public XMLTransformationNoArgCtorTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {EmployeeWithAddressAndTransformer.class});
        setControlDocument("org/eclipse/persistence/testing/jaxb/annotations/xmltransformation/employee.xml");
        setControlJSON("org/eclipse/persistence/testing/jaxb/annotations/xmltransformation/employee.json");
    }

    public Object getControlObject() {
        EmployeeWithAddressAndTransformer emp = new EmployeeWithAddressAndTransformer();
        emp.name = "John Smith";
        AddressNoCtor address = new AddressNoCtor("theStreet", "theCity");
        emp.address = address;
        return emp;
    }
}
