/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.dynamic.sessioneventlistener;

import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import junit.framework.TestCase;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.eclipse.persistence.sessions.SessionEventListener;
import org.eclipse.persistence.testing.jaxb.jaxbcontext.sessioneventlistener.Address;
import org.eclipse.persistence.testing.jaxb.jaxbcontext.sessioneventlistener.AddressAddedByEvent;
import org.eclipse.persistence.testing.jaxb.jaxbcontext.sessioneventlistener.TestSessionEventListener;

public class SessionEventListenerTestCases extends TestCase {

    private static final String XML = "<address><id>123</id></address>";

    private SessionEventListener sessionEventListener = null;


    @Override
    protected void setUp() throws Exception {
        sessionEventListener = new TestSessionEventListener();
        Address.INSTANTIATION_COUNTER = 0;
    }

    public void testOXM() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>(2);
        properties.put(JAXBContextProperties.SESSION_EVENT_LISTENER, sessionEventListener);
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, "org/eclipse/persistence/testing/jaxb/dynamic/sessioneventlistener/oxm.xml");
        JAXBContext jc = (JAXBContext) DynamicJAXBContextFactory.createContextFromOXM(this.getClass().getClassLoader(), properties);
        unmarshalTest(jc);
    }

    public void testXSD() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>(1);
        properties.put(JAXBContextProperties.SESSION_EVENT_LISTENER, sessionEventListener);

        ClassLoader classLoader = SessionEventListenerTestCases.class.getClassLoader();
        InputStream schemaStream = classLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/dynamic/sessioneventlistener/schema.xsd");
        JAXBContext jc = (JAXBContext) DynamicJAXBContextFactory.createContextFromXSD(schemaStream, null, classLoader, properties);
        unmarshalTest(jc);
    }

    private void unmarshalTest(JAXBContext jc) throws Exception{
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        Object test = unmarshaller.unmarshal(new StringReader(XML));
        assertEquals(getControlObject(), test);
    }

    private AddressAddedByEvent getControlObject() {
        AddressAddedByEvent control = new AddressAddedByEvent();
        control.setId(123);
        return control;
    }

}
