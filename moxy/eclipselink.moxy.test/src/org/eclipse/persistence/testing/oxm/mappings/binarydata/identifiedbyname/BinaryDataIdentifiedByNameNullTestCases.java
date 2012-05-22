/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.binarydata.identifiedbyname;

import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.binarydata.Employee;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.MyAttachmentMarshaller;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.MyAttachmentUnmarshaller;

/**
 * Not supported
 * base64Binary as attribute - no singlenode\xmlattribute
 */
public class BinaryDataIdentifiedByNameNullTestCases extends XMLWithJSONMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/binarydata/identifiedbyname/BinaryDataIdentifiedByNameNull.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/binarydata/identifiedbyname/BinaryDataIdentifiedByNameNull.json";
    public BinaryDataIdentifiedByNameNullTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        
        setProject(new BinaryDataIdentifiedByNameProject(null));
    }

    protected Object getControlObject() {
        Employee emp = Employee.example1();
        emp.setPhoto(null);
        return emp;
    }

    public void setUp() throws Exception {
        super.setUp();
        xmlMarshaller.setAttachmentMarshaller(new MyAttachmentMarshaller());
        xmlUnmarshaller.setAttachmentUnmarshaller(new MyAttachmentUnmarshaller());
    }
    
}
