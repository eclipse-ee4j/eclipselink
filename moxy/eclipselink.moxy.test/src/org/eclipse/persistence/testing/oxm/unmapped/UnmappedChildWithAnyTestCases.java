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
package org.eclipse.persistence.testing.oxm.unmapped;

import java.io.InputStream;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.mappings.XMLAnyObjectMapping;

public class UnmappedChildWithAnyTestCases extends OXTestCase {
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/unmapped/UnmappedChildWithAny.xml";
    private XMLUnmarshaller xmlUnmarshaller;

    public UnmappedChildWithAnyTestCases(String name) {
        super(name);
    }

    public void setUp() {
        MyUnmappedContentHandler.INSTANCE_COUNTER = 0;
    }

    public void testAnyMapping() {
        EmployeeProject employeeProject = new EmployeeProject();
        XMLAnyObjectMapping anyMapping = new XMLAnyObjectMapping();
        anyMapping.setAttributeName("any");
        employeeProject.getEmployeeDescriptor().addMapping(anyMapping);

        XMLContext xmlContext = new XMLContext(employeeProject);
        xmlUnmarshaller = xmlContext.createUnmarshaller();
        xmlUnmarshaller.setUnmappedContentHandlerClass(MyUnmappedContentHandler.class);

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
        xmlUnmarshaller.unmarshal(inputStream);

        assertEquals(2, MyUnmappedContentHandler.INSTANCE_COUNTER);
    }

    public void tearDown() {
        MyUnmappedContentHandler.INSTANCE_COUNTER = 0;
    }
}
