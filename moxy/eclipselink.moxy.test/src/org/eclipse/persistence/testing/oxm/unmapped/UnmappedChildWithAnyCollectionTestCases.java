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
package org.eclipse.persistence.testing.oxm.unmapped;

import java.io.InputStream;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.mappings.UnmarshalKeepAsElementPolicy;
import org.eclipse.persistence.oxm.mappings.XMLAnyCollectionMapping;

public class UnmappedChildWithAnyCollectionTestCases extends OXTestCase {
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/unmapped/UnmappedChildWithAnyCollection.xml";
    private XMLUnmarshaller xmlUnmarshaller;

    public UnmappedChildWithAnyCollectionTestCases(String name) {
        super(name);
    }

    public void setUp() {
        MyUnmappedContentHandler.INSTANCE_COUNTER = 0;
    }

    public void testWithoutKeepNoneAsElementMapping() {
        EmployeeProject employeeProject = new EmployeeProject();
        XMLAnyCollectionMapping anyCollectionMapping = new XMLAnyCollectionMapping();
        anyCollectionMapping.setAttributeName("anyCollection");
        anyCollectionMapping.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_NONE_AS_ELEMENT);
        employeeProject.getEmployeeDescriptor().addMapping(anyCollectionMapping);

        XMLContext xmlContext = new XMLContext(employeeProject);
        xmlUnmarshaller = xmlContext.createUnmarshaller();
        xmlUnmarshaller.setUnmappedContentHandlerClass(MyUnmappedContentHandler.class);

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
        Employee emp = (Employee)xmlUnmarshaller.unmarshal(inputStream);
        assertEquals(4, MyUnmappedContentHandler.INSTANCE_COUNTER);
    }

    public void testWithKeepAllAsElementMapping() {
        EmployeeProject employeeProject = new EmployeeProject();
        XMLAnyCollectionMapping anyCollectionMapping = new XMLAnyCollectionMapping();
        anyCollectionMapping.setAttributeName("anyCollection");
        anyCollectionMapping.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_ALL_AS_ELEMENT);
        employeeProject.getEmployeeDescriptor().addMapping(anyCollectionMapping);

        XMLContext xmlContext = new XMLContext(employeeProject);
        xmlUnmarshaller = xmlContext.createUnmarshaller();
        xmlUnmarshaller.setUnmappedContentHandlerClass(MyUnmappedContentHandler.class);

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
        xmlUnmarshaller.unmarshal(inputStream);

        assertEquals(0, MyUnmappedContentHandler.INSTANCE_COUNTER);
    }

    public void tearDown() {
        MyUnmappedContentHandler.INSTANCE_COUNTER = 0;
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.main(new String[] { "-c", "org.eclipse.persistence.testing.oxm.unmapped.UnmappedChildWithAnyCollectionTestCases" });
    }
}
