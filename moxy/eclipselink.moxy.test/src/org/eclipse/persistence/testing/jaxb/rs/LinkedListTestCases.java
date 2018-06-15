/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.5.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.rs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.rs.MOXyJsonProvider;

import junit.framework.TestCase;

public class LinkedListTestCases extends TestCase {


    @XmlSeeAlso({ObjectFactory.class})
    public static class ComplexWithXmlElementDecl {

        public int id;

        public ComplexWithXmlElementDecl() {
        }

        public ComplexWithXmlElementDecl(int id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object obj) {
            if(null == obj || obj.getClass() != this.getClass()) {
                return false;
            }
            ComplexWithXmlElementDecl test = (ComplexWithXmlElementDecl) obj;
            return id == test.id;
        }

    }
    @XmlRootElement(name="complex")
    public static class ComplexWithXmlRootElement {

        public int id;

        public ComplexWithXmlRootElement() {
        }

        public ComplexWithXmlRootElement(int id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object obj) {
            if(null == obj || obj.getClass() != this.getClass()) {
                return false;
            }
            ComplexWithXmlRootElement test = (ComplexWithXmlRootElement) obj;
            return id == test.id;
        }

    }

    @XmlRegistry
    public static class ObjectFactory {

        @XmlElementDecl(name="complex")
        public JAXBElement<ComplexWithXmlElementDecl> createComplexWithXmlElementDecl(ComplexWithXmlElementDecl foo) {
            return new JAXBElement<ComplexWithXmlElementDecl>(new QName("complex"), ComplexWithXmlElementDecl.class, foo);
        }

    }

    private static final String COMPLEX_JSON_ARRAY_WITHOUT_ROOT = "[{\"id\":0},{\"id\":1},{\"id\":2}]";
    private static final String COMPLEX_JSON_ARRAY_WITH_ROOT = "[{\"complex\":{\"id\":0}},{\"complex\":{\"id\":1}},{\"complex\":{\"id\":2}}]";
    public static final LinkedList<JAXBElement<ComplexWithXmlElementDecl>> COMPLEX_LINKED_LIST_WITH_XML_ELEMENT_DECL = new LinkedList<JAXBElement<ComplexWithXmlElementDecl>>();
    public static final LinkedList<ComplexWithXmlRootElement> COMPLEX_LINKED_LIST_WITH_XML_ROOT_ELEMENT = new LinkedList<ComplexWithXmlRootElement>();
    static {
        COMPLEX_LINKED_LIST_WITH_XML_ROOT_ELEMENT.add(new ComplexWithXmlRootElement(0));
        COMPLEX_LINKED_LIST_WITH_XML_ROOT_ELEMENT.add(new ComplexWithXmlRootElement(1));
        COMPLEX_LINKED_LIST_WITH_XML_ROOT_ELEMENT.add(new ComplexWithXmlRootElement(2));
    }

    private MOXyJsonProvider moxyJsonProvider;

    @Override
    protected void setUp() throws Exception {
        moxyJsonProvider = new MOXyJsonProvider();
    }

    public void testReadComplexLinkedListWithoutRoot() throws Exception {
        Field complexLinkedListField = LinkedListTestCases.class.getField("COMPLEX_LINKED_LIST_WITH_XML_ROOT_ELEMENT");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(COMPLEX_JSON_ARRAY_WITHOUT_ROOT.getBytes());
        LinkedList<ComplexWithXmlRootElement> test = (LinkedList<ComplexWithXmlRootElement>) moxyJsonProvider.readFrom((Class<Object>) complexLinkedListField.getType(), complexLinkedListField.getGenericType(), null, null, null, inputStream);
        assertTrue(equals(COMPLEX_LINKED_LIST_WITH_XML_ROOT_ELEMENT, test));
    }

    public void testReadComplexLinkedListWithRoot() throws Exception {
        moxyJsonProvider.setIncludeRoot(true);
        Field complexLinkedListField = LinkedListTestCases.class.getField("COMPLEX_LINKED_LIST_WITH_XML_ROOT_ELEMENT");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(COMPLEX_JSON_ARRAY_WITH_ROOT.getBytes());
        LinkedList<ComplexWithXmlRootElement> test = (LinkedList<ComplexWithXmlRootElement>) moxyJsonProvider.readFrom((Class<Object>) complexLinkedListField.getType(), complexLinkedListField.getGenericType(), null, null, null, inputStream);
        assertTrue(equals(COMPLEX_LINKED_LIST_WITH_XML_ROOT_ELEMENT, test));
    }

    public void testReadJAXBElementLinkedListWithoutRoot() throws Exception {
        Field complexLinkedListField = LinkedListTestCases.class.getField("COMPLEX_LINKED_LIST_WITH_XML_ELEMENT_DECL");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(COMPLEX_JSON_ARRAY_WITHOUT_ROOT.getBytes());
        LinkedList<JAXBElement<ComplexWithXmlElementDecl>> test =  (LinkedList<JAXBElement<ComplexWithXmlElementDecl>>) moxyJsonProvider.readFrom((Class<Object>) complexLinkedListField.getType(), complexLinkedListField.getGenericType(), null, null, null, inputStream);
        for(int x=0; x<test.size(); x++) {
            JAXBElement<ComplexWithXmlElementDecl> jaxbElement = test.get(x);
            assertEquals("", jaxbElement.getName().getLocalPart());
            assertEquals(x, jaxbElement.getValue().id);
        }
    }

    public void testReadJAXBElementLinkedListWithRoot() throws Exception {
        moxyJsonProvider.setIncludeRoot(true);
        Field complexLinkedListField = LinkedListTestCases.class.getField("COMPLEX_LINKED_LIST_WITH_XML_ELEMENT_DECL");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(COMPLEX_JSON_ARRAY_WITH_ROOT.getBytes());
        LinkedList<JAXBElement<ComplexWithXmlElementDecl>> test =  (LinkedList<JAXBElement<ComplexWithXmlElementDecl>>) moxyJsonProvider.readFrom((Class<Object>) complexLinkedListField.getType(), complexLinkedListField.getGenericType(), null, null, null, inputStream);
        for(int x=0; x<test.size(); x++) {
            JAXBElement<ComplexWithXmlElementDecl> jaxbElement = test.get(x);
            assertEquals("complex", jaxbElement.getName().getLocalPart());
            assertEquals(x, jaxbElement.getValue().id);
        }
    }

    public void testWriteComplexLinkedListWithoutRoot() throws Exception {
        Field complexLinkedListField = LinkedListTestCases.class.getField("COMPLEX_LINKED_LIST_WITH_XML_ROOT_ELEMENT");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        moxyJsonProvider.writeTo(COMPLEX_LINKED_LIST_WITH_XML_ROOT_ELEMENT, (Class<Object>) complexLinkedListField.getType(), complexLinkedListField.getGenericType(), null, null, null, outputStream);
        assertEquals(COMPLEX_JSON_ARRAY_WITHOUT_ROOT, new String(outputStream.toByteArray()));
    }

    public void testWriteComplexLinkedListWithRoot() throws Exception {
        moxyJsonProvider.setIncludeRoot(true);
        Field complexArrayField = LinkedListTestCases.class.getField("COMPLEX_LINKED_LIST_WITH_XML_ROOT_ELEMENT");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        moxyJsonProvider.writeTo(COMPLEX_LINKED_LIST_WITH_XML_ROOT_ELEMENT, (Class<Object>) complexArrayField.getType(), complexArrayField.getGenericType(), null, null, null, outputStream);
        assertEquals(COMPLEX_JSON_ARRAY_WITH_ROOT, new String(outputStream.toByteArray()));
    }

    public void testWriteJAXBElementLinkedListWithoutRoot() throws Exception {
        Field complexLinkedListField = LinkedListTestCases.class.getField("COMPLEX_LINKED_LIST_WITH_XML_ELEMENT_DECL");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        LinkedList<JAXBElement<ComplexWithXmlElementDecl>> jaxbElementLinkedList = new LinkedList<JAXBElement<ComplexWithXmlElementDecl>>();
        jaxbElementLinkedList.add(new JAXBElement(new QName(""), ComplexWithXmlElementDecl.class, new ComplexWithXmlElementDecl(0)));
        jaxbElementLinkedList.add(new JAXBElement(new QName(""), ComplexWithXmlElementDecl.class, new ComplexWithXmlElementDecl(1)));
        jaxbElementLinkedList.add(new JAXBElement(new QName(""), ComplexWithXmlElementDecl.class, new ComplexWithXmlElementDecl(2)));

        moxyJsonProvider.writeTo(jaxbElementLinkedList, (Class<Object>) complexLinkedListField.getType(), complexLinkedListField.getGenericType(), null, null, null, outputStream);
        assertEquals(COMPLEX_JSON_ARRAY_WITHOUT_ROOT, new String(outputStream.toByteArray()));
    }

    public void testWriteJAXBElementLinkedListWithRoot() throws Exception {
        moxyJsonProvider.setIncludeRoot(true);
        Field complexLinkedListField = LinkedListTestCases.class.getField("COMPLEX_LINKED_LIST_WITH_XML_ELEMENT_DECL");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        LinkedList<JAXBElement<ComplexWithXmlElementDecl>> jaxbElementLinkedList = new LinkedList<JAXBElement<ComplexWithXmlElementDecl>>();
        jaxbElementLinkedList.add(new JAXBElement(new QName("complex"), ComplexWithXmlElementDecl.class, new ComplexWithXmlElementDecl(0)));
        jaxbElementLinkedList.add(new JAXBElement(new QName("complex"), ComplexWithXmlElementDecl.class, new ComplexWithXmlElementDecl(1)));
        jaxbElementLinkedList.add(new JAXBElement(new QName("complex"), ComplexWithXmlElementDecl.class, new ComplexWithXmlElementDecl(2)));

        moxyJsonProvider.writeTo(jaxbElementLinkedList, (Class<Object>) complexLinkedListField.getType(), complexLinkedListField.getGenericType(), null, null, null, outputStream);
        assertEquals(COMPLEX_JSON_ARRAY_WITH_ROOT, new String(outputStream.toByteArray()));
    }

    private boolean equals(LinkedList control, LinkedList test) {
        if(control == test) {
            return true;
        }
        if(null == control || null == test) {
            return false;
        }
        if(control.size() != test.size()) {
            return false;
        }
        for(int x=0; x<control.size(); x++) {
            if(!control.get(x).equals(test.get(x))) {
                return false;
            }
        }
        return true;
    }

}
