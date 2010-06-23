/*******************************************************************************
* Copyright (c) 2010 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     bdoughan - June 18/2010 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.dynamic;

import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.eclipse.persistence.testing.oxm.OXTestCase;

import junit.framework.TestCase;

public class SingleListTestCases extends OXTestCase {

    private DynamicJAXBContext jaxbContext;

    public SingleListTestCases(String name) {
        super(name);
    }

    private static String XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/dynamic/SingleList.xsd";
    private static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/dynamic/SingleList.xml";

    @Override
    protected void setUp() throws Exception {
        InputStream schemaStream = Thread.currentThread().getContextClassLoader().getSystemResourceAsStream(XSD_RESOURCE);
        jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(schemaStream, null, null, null);
    }

    public void testUnmarshal() throws JAXBException {
        InputStream xmlStream = Thread.currentThread().getContextClassLoader().getSystemResourceAsStream(XML_RESOURCE);
        JAXBElement<DynamicEntity> jaxbElement = (JAXBElement<DynamicEntity>) jaxbContext.createUnmarshaller().unmarshal(xmlStream);

        DynamicEntity customer = jaxbElement.getValue();
        assertNotNull(customer);

        List<DynamicEntity> phoneNumbers = customer.<List<DynamicEntity>>get("phoneNumbers");
        assertEquals(3, phoneNumbers.size());
        DynamicEntity firstPhoneNumber = phoneNumbers.get(0);
        assertEquals("work", firstPhoneNumber.get("type"));
    }

}