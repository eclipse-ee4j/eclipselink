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
//  - rbarkhouse - 07 December 2012 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.events;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

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
        assertEquals(1, mListener.beforeClasses.size());
        assertEquals(JAXBElement.class, mListener.beforeClasses.get(0));
    }

    public void testAfterMarshalUnmapped() throws Exception {
        marshaller.marshal(getControlObjectUnmapped(), new ByteArrayOutputStream());

        assertEquals(1, mListener.afterClasses.size());
        assertEquals(JAXBElement.class, mListener.afterClasses.get(0));
    }

    public void testBeforeMarshalMapped() throws Exception {
        marshaller.marshal(getControlObjectMapped(), new ByteArrayOutputStream());

        assertEquals(1, mListener.beforeClasses.size());
        assertEquals(Employee.class, mListener.beforeClasses.get(0));
    }

    public void testAfterMarshalMapped() throws Exception {
        marshaller.marshal(getControlObjectMapped(), new ByteArrayOutputStream());
        assertEquals(1, mListener.afterClasses.size());
        assertEquals(Employee.class, mListener.afterClasses.get(0));
    }

    public void testBeforeMarshalMappedJAXBElement() throws Exception {
        Object obj = getControlObjectMapped();
        JAXBElement elem = new JAXBElement(new QName("root"), obj.getClass(), obj);
        marshaller.marshal(elem, new ByteArrayOutputStream());

        assertEquals(2, mListener.beforeClasses.size());
        assertEquals(JAXBElement.class, mListener.beforeClasses.get(0));
        assertEquals(Employee.class, mListener.beforeClasses.get(1));
    }

    public void testAfterMarshalMappedJAXBElement() throws Exception {
        Object obj = getControlObjectMapped();
        JAXBElement elem = new JAXBElement(new QName("root"), obj.getClass(), obj);

        marshaller.marshal(elem, new ByteArrayOutputStream());

        assertEquals(2, mListener.afterClasses.size());
        assertEquals(Employee.class, mListener.afterClasses.get(0));
        assertEquals(JAXBElement.class, mListener.afterClasses.get(1));
     }



    // ========================================================================

    private class MarshalListenerImpl extends Marshaller.Listener {
        public List<Class> beforeClasses;
        public List<Class> afterClasses;

        public MarshalListenerImpl(){
            beforeClasses = new ArrayList<Class>();
            afterClasses = new ArrayList<Class>();
        }

        @Override
        public void beforeMarshal(Object source) {
            beforeClasses.add(source.getClass());
        }

        @Override
        public void afterMarshal(Object source) {
            afterClasses.add(source.getClass());
        }
    }

}
