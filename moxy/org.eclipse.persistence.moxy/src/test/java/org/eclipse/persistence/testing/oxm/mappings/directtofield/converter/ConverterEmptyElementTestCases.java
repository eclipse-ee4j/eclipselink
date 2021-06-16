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
// Denise Smith - September 22 /2009
package org.eclipse.persistence.testing.oxm.mappings.directtofield.converter;

import java.io.InputStream;

import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.defaultnullvalue.xmlelement.DefaultNullValueElementProject;
import org.w3c.dom.Document;

public class ConverterEmptyElementTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directtofield/converter/employee_empty.xml";

    public ConverterEmptyElementTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        Project aProject = new ConverterProject();
        setProject(aProject);
    }

    public void setUp() throws Exception{
        super.setUp();
        MyConverter.HIT_CONVERTER = false;
    }

    protected Object getControlObject() {
        Employee emp = new Employee();
        emp.firstName = "Bill";
        emp.lastName = "Jones";
        emp.gender ="";

        return emp;
    }

    public void xmlToObjectTest(Object testObject) throws Exception{
        super.xmlToObjectTest(testObject);
        assertTrue(MyConverter.HIT_CONVERTER);
    }


}
