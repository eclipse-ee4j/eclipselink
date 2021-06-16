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
//     rbarkhouse - 2009-10-02 16:25:00 - initial implementation
package org.eclipse.persistence.testing.oxm.mappings.directcollection.reuse;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class DirectCollectionReuseTestCases extends XMLMappingTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directcollection/arraylist/DirectCollectionArrayList.xml";
    private final static int CONTROL_ID = 123;
    private final static String CONTROL_RESPONSIBILITY1 = "make the coffee";
    private final static String CONTROL_RESPONSIBILITY2 = "do the dishes";
    private final static String CONTROL_RESPONSIBILITY3 = "take out the garbage";

    public DirectCollectionReuseTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setProject(new DirectCollectionReuseProject());
    }

    protected Object getControlObject() {
        List responsibilities = new LinkedList();
        responsibilities.add(CONTROL_RESPONSIBILITY1);
        responsibilities.add(CONTROL_RESPONSIBILITY2);
        responsibilities.add(CONTROL_RESPONSIBILITY3);

        Employee employee = new Employee();
        employee.setID(CONTROL_ID);
        employee.setResponsibilities(responsibilities);
        return employee;
    }

    public void testContainerReused() throws Exception {
        URL url = ClassLoader.getSystemResource(resourceName);
        Employee testObject = (Employee) xmlUnmarshaller.unmarshal(url);

        assertEquals("This mapping's container was not reused.", LinkedList.class, testObject.getResponsibilities().getClass());
    }

}
