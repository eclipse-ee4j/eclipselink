/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Blaise Doughan - 2.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlmarshaller.locator;

import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.oxm.OXTestCase;

public abstract class LocatorTestCase extends OXTestCase {

    private static final String XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlmarshaller/locator/schema.xsd";

    private Marshaller marshaller;
    private Object root;
    protected Child child;

    public LocatorTestCase(String name) {
        super(name);
    }

    public abstract Class[] getClasses();

    public abstract Object setupRootObject();

    @Override
    protected void setUp() throws Exception {
        SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
        InputStream schemaStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(XSD_RESOURCE);
        Schema s = sf.newSchema(new StreamSource(schemaStream));

        marshaller = JAXBContextFactory.createContext(getClasses(), null).createMarshaller();
        marshaller.setSchema(s);

        child = new Child();
        child.setName("123456789");

        root = setupRootObject();
    }

    public void testMarshalLocatorObjects() throws JAXBException {
        TestValidationEventHandler veh = new TestValidationEventHandler();
        marshaller.setEventHandler(veh);
        marshaller.marshal(root, new StringWriter());

        assertSame(root, veh.getValidationEvents().get(0).getLocator().getObject());
        assertSame(root, veh.getValidationEvents().get(1).getLocator().getObject());
        assertSame(child, veh.getValidationEvents().get(2).getLocator().getObject());
        assertSame(child, veh.getValidationEvents().get(3).getLocator().getObject());
        assertSame(root,veh.getValidationEvents().get(4).getLocator().getObject());
    }

}
