/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - 07 December 2012 - 2.4 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.events;

import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.eclipse.persistence.jaxb.JAXBContextFactory;

public class JAXBElementTestCases extends TestCase {

    private Marshaller marshaller;
    private MarshalListenerImpl mListener;
    
    public JAXBElementTestCases(String name) throws Exception {
        super(name);
    }

    @Override
    public String getName() {
        return "Marshaller.Listener tests with JAXBElements " + super.getName();
    }
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        
        mListener = new MarshalListenerImpl();
        
        JAXBContext context = JAXBContextFactory.createContext(new Class[] { Employee.class }, null);
        
        marshaller = context.createMarshaller();
        marshaller.setListener(mListener);
    }

    private Object getControlObjectUnmapped() {
        return new JAXBElement<Integer>(new QName("year"), Integer.class, new Integer(1942));
    }

    private Object getControlObjectMapped() {
        return new Employee();
    }

    public void testBeforeMarshalUnmapped() throws Exception {
        marshaller.marshal(getControlObjectUnmapped(), new ByteArrayOutputStream());

        assertEquals(JAXBElement.class, mListener.beforeClass);
    }

    public void testAfterMarshalUnmapped() throws Exception {
        marshaller.marshal(getControlObjectUnmapped(), new ByteArrayOutputStream());

        assertEquals(JAXBElement.class, mListener.afterClass);
    }

    public void testBeforeMarshalMapped() throws Exception {
        marshaller.marshal(getControlObjectMapped(), new ByteArrayOutputStream());

        assertEquals(Employee.class, mListener.beforeClass);
    }

    public void testAfterMarshalMapped() throws Exception {
        marshaller.marshal(getControlObjectMapped(), new ByteArrayOutputStream());

        assertEquals(Employee.class, mListener.afterClass);
    }

    // ========================================================================
    
    private class MarshalListenerImpl extends Marshaller.Listener {
        public Class beforeClass, afterClass;
        
        @Override
        public void beforeMarshal(Object source) {
            beforeClass = source.getClass();
        }

        @Override
        public void afterMarshal(Object source) {
            afterClass = source.getClass();
        }       
    }

}