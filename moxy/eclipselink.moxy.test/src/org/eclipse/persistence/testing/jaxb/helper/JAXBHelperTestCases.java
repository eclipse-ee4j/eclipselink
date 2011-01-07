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
 *     dmccann - September 15/2009 - 1.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.helper;

import javax.xml.bind.Binder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.jaxb.JAXBBinder;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBHelper;
import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.oxm.XMLBinder;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;

import junit.framework.TestCase;

public class JAXBHelperTestCases extends TestCase {
    private JAXBContext jaxbContext;

    public JAXBHelperTestCases(String name) throws Exception {
        super(name);
    }

    public void setUp() throws Exception {
        Class[] classes = new Class[1];
        classes[0] = Customer.class;
        jaxbContext = JAXBContextFactory.createContext(classes, null);
    }
    
    public void testCastToJAXBContext() {
        assertTrue(JAXBHelper.getJAXBContext(jaxbContext) instanceof org.eclipse.persistence.jaxb.JAXBContext);
    }

    public void testCastToJAXBUnmarshaller() throws Exception {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        assertTrue(JAXBHelper.getUnmarshaller(unmarshaller) instanceof JAXBUnmarshaller);
    }

    public void testCastToJAXBMarshaller() throws Exception {
        Marshaller marshaller = jaxbContext.createMarshaller();
        assertTrue(JAXBHelper.getMarshaller(marshaller) instanceof JAXBMarshaller);
    }

    public void testCastToJAXBBinder() throws Exception {
        Binder binder = jaxbContext.createBinder();
        assertTrue(JAXBHelper.getBinder(binder) instanceof JAXBBinder);
    }

    public void testUnwrapJAXBContextToJAXBImpl() {
        assertTrue(JAXBHelper.unwrap(jaxbContext, org.eclipse.persistence.jaxb.JAXBContext.class) instanceof org.eclipse.persistence.jaxb.JAXBContext);
    }

    public void testUnwrapJAXBContextToXMLContext() {
        assertTrue(JAXBHelper.unwrap(jaxbContext, XMLContext.class) instanceof XMLContext);
    }

    public void testUnwrapJAXBUnmarshallerToJAXBImpl() throws Exception {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        assertTrue(JAXBHelper.unwrap(unmarshaller, JAXBUnmarshaller.class) instanceof JAXBUnmarshaller);
    }

    public void testUnwrapJAXBUnmarshallerToXMLUnmarshaller() throws Exception {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        assertTrue(JAXBHelper.unwrap(unmarshaller, XMLUnmarshaller.class) instanceof XMLUnmarshaller);
    }

    public void testUnwrapJAXBMarshallerToJAXBImpl() throws Exception {
        Marshaller marshaller = jaxbContext.createMarshaller();
        assertTrue(JAXBHelper.unwrap(marshaller, JAXBMarshaller.class) instanceof JAXBMarshaller);
    }

    public void testUnwrapJAXBMarshallerToXMLMarshaller() throws Exception {
        Marshaller marshaller = jaxbContext.createMarshaller();
        assertTrue(JAXBHelper.unwrap(marshaller, XMLMarshaller.class) instanceof XMLMarshaller);
    }

    public void testUnwrapJAXBBinderToJAXBImpl() throws Exception {
        Binder binder = jaxbContext.createBinder();
        assertTrue(JAXBHelper.unwrap(binder, JAXBBinder.class) instanceof JAXBBinder);
    }

    public void testUnwrapJAXBBinderToXMLBinder() throws Exception {
        Binder binder = jaxbContext.createBinder();
        assertTrue(JAXBHelper.unwrap(binder, XMLBinder.class) instanceof XMLBinder);
    }
}
