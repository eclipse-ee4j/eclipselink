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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.directtofield.cdata;

import java.math.BigInteger;
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class DirectToFieldCDATATestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directtofield/cdata/employee.xml";
    private final static String CONTROL_FIRST_NAME = "Jane";
    private final static String CONTROL_LAST_NAME = "Do]]>e";
    private final static String CONTROL_DATA = "A string containing bad xml characters like < and > and /> and stuff like that";
    private final static String CONTROL_NESTED_CDATA = "<![CDATA[nested]]>";

    public DirectToFieldCDATATestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setProject(new DirectToFieldCDATAProject());
    }

    protected Object getControlObject() {
        Employee emp = new Employee();
        emp.firstName = CONTROL_FIRST_NAME;
        emp.lastName = CONTROL_LAST_NAME;
        emp.data = CONTROL_DATA;
        emp.nestedCData = CONTROL_NESTED_CDATA;

        return emp;
    }

    public void testObjectToContentHandler() throws Exception {
        //CDATA not supported by content handlers
    }

}

