/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//  - Denise Smith - September 2013
package org.eclipse.persistence.testing.jaxb.xmladapter.noarg;

import javax.xml.bind.Marshaller;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class NoArgCtorAdapterTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/noarg.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/noarg.json";

    public NoArgCtorAdapterTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[] {Root.class, ObjectFactory.class};
        setClasses(classes);
    }

    protected Object getControlObject() {
        Root root = new Root();
        root.name = "Bob";
        root.theThing = new Something("testing");
        return root;
    }

    public void testContextCreation(){
        assertNotNull(((AbstractSession)(((JAXBContext)jaxbContext).getXMLContext().getSessions().get(0))).getDescriptor(Root.class));
        assertNotNull(((AbstractSession)(((JAXBContext)jaxbContext).getXMLContext().getSessions().get(0))).getDescriptor(SomethingElse.class));
        assertNull(((AbstractSession)(((JAXBContext)jaxbContext).getXMLContext().getSessions().get(0))).getDescriptor(Something.class));
    }

}
