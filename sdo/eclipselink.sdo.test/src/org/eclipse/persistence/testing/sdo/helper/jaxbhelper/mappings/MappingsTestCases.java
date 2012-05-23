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
*     bdoughan - Jan 27/2009 - 1.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.sdo.helper.jaxbhelper.mappings;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.sdo.helper.jaxb.JAXBHelperContext;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

public class MappingsTestCases extends SDOTestCase {

    private static final String XML_SCHEMA = "org/eclipse/persistence/testing/sdo/helper/jaxbhelper/mappings/Mappings.xsd";
    private static final String XML_INPUT = "org/eclipse/persistence/testing/sdo/helper/jaxbhelper/mappings/Mappings.xml";

    private JAXBHelperContext jaxbHelperContext;

    public MappingsTestCases(String name) {
        super(name);
    }

    public void setUp() {
        MappingsProject project = new MappingsProject();
        XMLContext xmlContext = new XMLContext(project);
        JAXBContext jaxbContext = new JAXBContext(xmlContext);
        jaxbHelperContext = new JAXBHelperContext(jaxbContext);
        
        InputStream xsd = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_SCHEMA);
        jaxbHelperContext.getXSDHelper().define(xsd, null);
    }

    public void test1() throws IOException {
        InputStream xml = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_INPUT);
        XMLDocument xmlDocument = jaxbHelperContext.getXMLHelper().load(xml);

        DataObject rootDO = xmlDocument.getRootObject();
        assertNotNull(rootDO);
        Root root = (Root) jaxbHelperContext.unwrap(rootDO);
        assertNotNull(root);
        assertEquals("123", root.getId());
        assertEquals("NAME", root.getName());

        Type t = jaxbHelperContext.getType(Root.class);
        Property simpleListProp = t.getProperty("simple-list");
        assertNotNull(simpleListProp);

        List doSimpleList = rootDO.getList(simpleListProp);
        assertEquals(2, doSimpleList.size());
        assertTrue(doSimpleList.contains("FOO"));
        assertTrue(doSimpleList.contains("BAR"));

        List simpleList = root.getSimpleList();
        assertEquals(2, simpleList.size());
        assertTrue(simpleList.contains("FOO"));
        assertTrue(simpleList.contains("BAR"));        

        DataObject child1DO = rootDO.getDataObject("child1");
        assertNotNull(child1DO);
        Child1 child1 = (Child1) jaxbHelperContext.unwrap(child1DO);
        assertNotNull(child1);

        List child2DOCollection = rootDO.getList("child2");
        assertEquals(2, child2DOCollection.size());
        List child2Collection = jaxbHelperContext.unwrap(child2DOCollection);
        assertEquals(2, child2Collection.size());

        Child2 child2A = (Child2) child2Collection.get(0);
        assertEquals(child1, child2A.getChild1());
        assertTrue(child1.getChild2Collection().contains(child2A));

        Child2 child2B = (Child2) child2Collection.get(1);
        assertEquals(child1, child2B.getChild1());
        assertTrue(child1.getChild2Collection().contains(child2B));
    }

    public void tearDown() {
    }

}
