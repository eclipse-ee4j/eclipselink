/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     rbarkhouse - 2009-06-19 14:33:00 - initial implementation
package org.eclipse.persistence.testing.oxm.mappings.directtofield.singleattribute.xmlelement;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.singleattribute.Employee;
import org.w3c.dom.Node;

public class EmptyElementEmptyStringTestCases extends XMLMappingTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directtofield/singleattribute/xmlelement/EmptyElementEmptyString.xml";
    private final static String CONTROL_ID = "";

    public EmptyElementEmptyStringTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setProject(new EmptyElementEmptyStringProject());
        setShouldRemoveEmptyTextNodesFromControlDoc(false);
    }

    protected Object getControlObject() {
        EmptyElementEmptyStringEmployee employee = new EmptyElementEmptyStringEmployee();
        employee.setID(CONTROL_ID);
        return employee;
    }

}
