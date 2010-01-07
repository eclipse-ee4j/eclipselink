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
*     bdoughan - Feb 3/2009 - 1.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.sdo.helper.jaxbhelper.identity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.sdo.helper.jaxb.JAXBHelperContext;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XSDHelper;

public class IdentityTestCases extends SDOTestCase {

    private static final String XML_SCHEMA = "org/eclipse/persistence/testing/sdo/helper/jaxbhelper/identity/Identity.xsd";

    private JAXBHelperContext jaxbHelperContext;

    public IdentityTestCases(String name) {
        super(name);
    }

    public void setUp() {
        IdentityProject project = new IdentityProject();
        XMLContext xmlContext = new XMLContext(project);
        JAXBContext jaxbContext = new JAXBContext(xmlContext);
        jaxbHelperContext = new JAXBHelperContext(jaxbContext);

        InputStream xsd = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_SCHEMA);
        jaxbHelperContext.getXSDHelper().define(xsd, null);
    }

    public void testIdentityWrapForEqualObjects() {
        Root root1 = new Root();
        root1.setName("Jane");

        Root root2 = new Root();
        root2.setName("Jane");

        assertEquals(root1, root2);
        assertNotSame(root1, root2);

        DataObject root1DO = jaxbHelperContext.wrap(root1);
        DataObject root2DO = jaxbHelperContext.wrap(root2);
        assertNotSame(root1DO, root2DO);
    }

    public void tearDown() {
    }

}
