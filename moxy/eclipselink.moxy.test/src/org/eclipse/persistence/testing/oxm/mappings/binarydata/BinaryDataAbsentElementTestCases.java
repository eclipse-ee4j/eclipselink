/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     rbarkhouse - 2009-04-22 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.binarydata;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataMapping;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.binarydata.Employee;

public class BinaryDataAbsentElementTestCases extends XMLWithJSONMappingTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/binarydata/BinaryDataAbsentElement.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/binarydata/BinaryDataAbsentElement.json";

    public BinaryDataAbsentElementTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Project p = new BinaryDataEmptyElementProject();
        DatabaseMapping mapping = p.getClassDescriptor(Employee.class).getMappingForAttributeName("photo");        
        ((XMLBinaryDataMapping)mapping).getNullPolicy().setMarshalNullRepresentation(XMLNullRepresentationType.ABSENT_NODE);
        setProject(p);
    }

    protected Object getControlObject() {
        Employee emp = new Employee(123);
        emp.setPhoto(null);
        return emp;
    }
    
}