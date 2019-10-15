/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     rbarkhouse - 2009-04-22 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.binarydata;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.binarydata.Employee;

public class BinaryDataEmptyElementEmptyNodeFalseTestCases extends XMLWithJSONMappingTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/binarydata/BinaryDataEmptyElement.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/binarydata/BinaryDataEmptyElementFalse.json";

    public BinaryDataEmptyElementEmptyNodeFalseTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Project p = new BinaryDataEmptyElementProject();
        DatabaseMapping mapping = p.getClassDescriptor(Employee.class).getMappingForAttributeName("photo");
        ((XMLBinaryDataMapping)mapping).getNullPolicy().setNullRepresentedByEmptyNode(false);
        setProject(p);
    }

    protected Object getControlObject() {
        Employee emp = new Employee(123);
        emp.setPhoto(new byte[0]);
        return emp;
    }

}
