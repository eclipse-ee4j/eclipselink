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
*     bdoughan - Jan 27/2009 - 1.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.sdo.helper.jaxbhelper.copyhelper;

import java.io.InputStream;
import java.util.List;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.sdo.helper.jaxb.JAXBHelperContext;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

import commonj.sdo.DataObject;

public class CopyHelperTestCases extends SDOTestCase {

    private static final String XML_SCHEMA = "org/eclipse/persistence/testing/sdo/helper/jaxbhelper/copyhelper/CopyHelper.xsd";

    private JAXBHelperContext jaxbHelperContext;

    public CopyHelperTestCases(String name) {
        super(name);
    }

    public void setUp() {
        CopyHelperProject project = new CopyHelperProject();
        XMLContext xmlContext = new XMLContext(project);
        JAXBContext jaxbContext = new JAXBContext(xmlContext);
        jaxbHelperContext = new JAXBHelperContext(jaxbContext);
        
        InputStream xsd = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_SCHEMA);
        jaxbHelperContext.getXSDHelper().define(xsd, null);
    }

    public void testCopy1() {
        DataObject rootDO = jaxbHelperContext.getDataFactory().create("urn:copy", "root");
        DataObject child1DO = rootDO.createDataObject("child1");
        DataObject child2DO = rootDO.createDataObject("child2");

        DataObject rootDOCopy = jaxbHelperContext.getCopyHelper().copy(rootDO);

        Root root = (Root) jaxbHelperContext.unwrap(rootDO);
        Root rootCopy = (Root) jaxbHelperContext.unwrap(rootDOCopy);

        assertTrue(jaxbHelperContext.getEqualityHelper().equal(rootDO, rootDOCopy));

        assertNotSame(root, rootCopy);
        assertNotNull(rootCopy);

        assertNotSame(root.getChild1(), rootCopy.getChild1());
        assertNotNull(rootCopy.getChild1());
        
        assertNotSame(root.getChild2(), rootCopy.getChild2());
        assertNotNull(rootCopy.getChild2());
    }

    public void testCopy2() {
        Root root = new Root();
        Child1 child1 = new Child1();
        root.setChild1(child1);
        Child2 child2 = new Child2();
        root.setChild2(child2);

        DataObject rootDO = jaxbHelperContext.wrap(root);

        DataObject rootDOCopy = jaxbHelperContext.getCopyHelper().copy(rootDO);

        Root rootCopy = (Root) jaxbHelperContext.unwrap(rootDOCopy);

        assertTrue(jaxbHelperContext.getEqualityHelper().equal(rootDO, rootDOCopy));

        assertNotSame(root, rootCopy);
        assertNotNull(rootCopy);

        assertNotSame(root.getChild1(), rootCopy.getChild1());
        assertNotNull(rootCopy.getChild1());

        assertNotSame(root.getChild2(), rootCopy.getChild2());
        assertNotNull(rootCopy.getChild2());
    }

    public void tearDown() {
    }

}
