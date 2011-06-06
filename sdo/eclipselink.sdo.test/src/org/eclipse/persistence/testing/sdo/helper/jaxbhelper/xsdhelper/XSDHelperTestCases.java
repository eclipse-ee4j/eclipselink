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
package org.eclipse.persistence.testing.sdo.helper.jaxbhelper.xsdhelper;

import java.io.InputStream;
import java.util.List;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.sdo.helper.jaxb.JAXBHelperContext;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.helper.XSDHelper;

public class XSDHelperTestCases extends SDOTestCase {

    private static final String XML_SCHEMA = "org/eclipse/persistence/testing/sdo/helper/jaxbhelper/xsdhelper/XSDHelper.xsd";

    private JAXBHelperContext jaxbHelperContext;

    public XSDHelperTestCases(String name) {
        super(name);
    }

    public void setUp() {
        XSDHelperProject project = new XSDHelperProject();
        XMLContext xmlContext = new XMLContext(project);
        JAXBContext jaxbContext = new JAXBContext(xmlContext);
        jaxbHelperContext = new JAXBHelperContext(jaxbContext);

        InputStream xsd = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_SCHEMA);
        jaxbHelperContext.getXSDHelper().define(xsd, null);
    }

    public void testCreateTypeFromGlobalComplexType() {
        DataObject rootDO = jaxbHelperContext.getDataFactory().create("urn:xsd", "root");
        assertNotNull(rootDO);

        Root root = (Root) jaxbHelperContext.unwrap(rootDO);
        assertNotNull(root);
        assertSame(Root.class, root.getClass());
    }

    public void testCreateTypeFromGlobaComplexTypeRenamed() {
        DataObject child1DO = jaxbHelperContext.getDataFactory().create("urn:xsd", "renamedChild1");
        assertNotNull(child1DO);

        Child1 child1 = (Child1) jaxbHelperContext.unwrap(child1DO);
        assertNotNull(child1);
        assertSame(Child1.class, child1.getClass());
    }

    public void testCreateTypeFromGlobalElement() {
        DataObject child2DO = jaxbHelperContext.getDataFactory().create("urn:xsd", "child2");
        assertNotNull(child2DO);

        Child2 child2 = (Child2) jaxbHelperContext.unwrap(child2DO);
        assertNotNull(child2);
        assertSame(Child2.class, child2.getClass());
    }

    public void tearDown() {
    }

}
