/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - Denise Smith - September 2013
 ******************************************************************************/
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