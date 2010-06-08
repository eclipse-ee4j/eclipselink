/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.events;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class RootWithCompositeObjectTestCases extends XMLMappingTestCases {
    public MarshalListenerImpl listener;
    public UnmarshalListenerImpl unmarshalListener;
    public ArrayList expectedMarshalEvents;
    public ArrayList expectedUnmarshalEvents;
    
    public RootWithCompositeObjectTestCases(String name) throws Exception {
        super(name);
        setProject(new EmployeeProject());
        setControlDocument("org/eclipse/persistence/testing/oxm/events/composite_object.xml");

        expectedMarshalEvents = new ArrayList();
        expectedMarshalEvents.add(MarshalListenerImpl.EMPLOYEE_BEFORE_MARSHAL);
        expectedMarshalEvents.add(MarshalListenerImpl.ADDRESS_BEFORE_MARSHAL);
        expectedMarshalEvents.add(MarshalListenerImpl.ADDRESS_AFTER_MARSHAL);
        expectedMarshalEvents.add(MarshalListenerImpl.EMPLOYEE_AFTER_MARSHAL);
        
        expectedUnmarshalEvents = new ArrayList();
        expectedUnmarshalEvents.add(UnmarshalListenerImpl.EMPLOYEE_BEFORE_UNMARSHAL);
        expectedUnmarshalEvents.add(UnmarshalListenerImpl.ADDRESS_BEFORE_UNMARSHAL);
        expectedUnmarshalEvents.add(UnmarshalListenerImpl.ADDRESS_AFTER_UNMARSHAL);
        expectedUnmarshalEvents.add(UnmarshalListenerImpl.EMPLOYEE_AFTER_UNMARSHAL);
    }
    
    public void setUp() throws Exception {
        super.setUp();
        listener = new MarshalListenerImpl();
        unmarshalListener = new UnmarshalListenerImpl();
        
        this.xmlMarshaller.setMarshalListener(listener);
        this.xmlUnmarshaller.setUnmarshalListener(unmarshalListener);
    }
    public void xmlToObjectTest(Object testObject) throws Exception {
        super.xmlToObjectTest(testObject);
        assertTrue("Expected sequence of Unmarshal events not found", expectedUnmarshalEvents.equals(unmarshalListener.events));
    }
    
    public void objectToXMLDocumentTest(Document testDocument) throws Exception {
        super.objectToXMLDocumentTest(testDocument);
        assertTrue("Expected sequence of Marshal events not found", expectedMarshalEvents.equals(listener.events));
    }

    public Object getControlObject() {
        Employee employee = new Employee();
        Address address = new Address();
        address.street = "2201 Riverside Drive";
        employee.address = address;
        return employee;
    }
}
