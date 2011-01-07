/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.mappings.directcollection.singlenode.xmlattribute;

import junit.textui.TestRunner;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.arraylist.Employee;

public class DirectCollectionSingleNodeXMLAttributeMethodAccessingTestCases extends DirectCollectionSingleNodeXMLAttributeTestCases {
    public DirectCollectionSingleNodeXMLAttributeMethodAccessingTestCases(String name) throws Exception {
        super(name);
        Project p = new DirectCollectionSingleNodeXMLAttributeProject();
        p.getDescriptor(Employee.class).getMappingForAttributeName("id").setGetMethodName("getID");
        p.getDescriptor(Employee.class).getMappingForAttributeName("id").setSetMethodName("setID");
        
        p.getDescriptor(Employee.class).getMappingForAttributeName("responsibilities").setGetMethodName("getResponsibilities");
        p.getDescriptor(Employee.class).getMappingForAttributeName("responsibilities").setSetMethodName("setResponsibilities");
        setProject(p);
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.directcollection.singlenode.xmlattribute.DirectCollectionSingleNodeXMLAttributeMethodAccessingTestCases" };
        TestRunner.main(arguments);
    }
    
     public void xmlToObjectTest(Object testObject) throws Exception {
        super.xmlToObjectTest(testObject);
        assertEquals(1, ((Employee)testObject).getIdSetCounter());
        assertEquals(1, ((Employee)testObject).getResponsibilitiesSetCounter());       
    }
    
}
