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
//     Blaise Doughan - 2.4.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlmarshaller;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.Binder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBBinder;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.xmlmarshaller.ListenerMarshal.MarshalEvent;
import org.eclipse.persistence.testing.jaxb.xmlmarshaller.ListenerUnmarshal.UnmarshalEvent;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import junit.framework.TestCase;

public class ListenerTestCases extends TestCase {

    private static String ROOT_XML = "<root><name>Foo</name><child/></root>";
    private static String ELEMENT_DECL_XML = "<ledo><name>Bar</name><child/></ledo>";

    private JAXBContext jc;

    public JAXBElement<ListenerElementDeclObject> getControlJAXBElement() {
        ListenerElementDeclObject ledo = new ListenerElementDeclObject();
        ledo.name = "Bar";
        ledo.child = new ListenerChildObject();
        ListenerObjectFactory objectFactory = new ListenerObjectFactory();
        return objectFactory.createElementDecl(ledo);
    }

    public ListenerRootObject getControlRootObject() {
        ListenerRootObject rootObject = new ListenerRootObject();
        rootObject.name = "Foo";
        rootObject.child = new ListenerChildObject();
        return rootObject;
    }

    @Override
    protected void setUp() throws Exception {
        jc = JAXBContextFactory.createContext(new Class[] {ListenerRootObject.class, ListenerElementDeclObject.class, ListenerObjectFactory.class}, null);
    }

    private void testMarshalListenerElementDecl(JAXBElement<ListenerElementDeclObject> jaxbElement, Marshaller marshaller, ListenerMarshal listener) throws Exception {
        List<MarshalEvent> events = listener.getEvents();
        assertEquals(6, events.size());

        assertEquals(true, events.get(0).isBeforeEvent());
        assertSame(jaxbElement, events.get(0).getSource());

        assertEquals(true, events.get(1).isBeforeEvent());
        assertSame(jaxbElement.getValue(), events.get(1).getSource());
        assertSame(marshaller, jaxbElement.getValue().beforeMarshalMarshaller);

        assertEquals(true, events.get(2).isBeforeEvent());
        assertSame(jaxbElement.getValue().child, events.get(2).getSource());
        assertSame(marshaller, jaxbElement.getValue().beforeMarshalMarshaller);

        assertEquals(false, events.get(3).isBeforeEvent());
        assertSame(jaxbElement.getValue().child, events.get(3).getSource());
        assertSame(marshaller, jaxbElement.getValue().child.afterMarshalMarshaller);

        assertEquals(false, events.get(4).isBeforeEvent());
        assertSame(jaxbElement.getValue(), events.get(4).getSource());
        assertSame(marshaller, jaxbElement.getValue().afterMarshalMarshaller);

        assertEquals(false, events.get(5).isBeforeEvent());
        assertSame(jaxbElement, events.get(5).getSource());
    }

    public void testMarshalListenerElementDecl_ContentHandler() throws Exception {
        JAXBElement<ListenerElementDeclObject> jaxbElement = getControlJAXBElement();

        Marshaller marshaller = jc.createMarshaller();
        ListenerMarshal listener = new ListenerMarshal();
        marshaller.setListener(listener);
        marshaller.marshal(jaxbElement, new DefaultHandler());

        testMarshalListenerElementDecl(jaxbElement, marshaller, listener);
    }

    public void testMarshalListenerElementDecl_Node() throws Exception {
        JAXBElement<ListenerElementDeclObject> jaxbElement = getControlJAXBElement();

        Marshaller marshaller = jc.createMarshaller();
        ListenerMarshal listener = new ListenerMarshal();
        marshaller.setListener(listener);

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.newDocument();
        marshaller.marshal(jaxbElement, document);

        testMarshalListenerElementDecl(jaxbElement, marshaller, listener);
    }

    public void testMarshalListenerElementDecl_OutputStream() throws Exception {
        JAXBElement<ListenerElementDeclObject> jaxbElement = getControlJAXBElement();

        Marshaller marshaller = jc.createMarshaller();
        ListenerMarshal listener = new ListenerMarshal();
        marshaller.setListener(listener);
        marshaller.marshal(jaxbElement, new ByteArrayOutputStream());

        testMarshalListenerElementDecl(jaxbElement, marshaller, listener);
    }

    public void testMarshalListenerElementDecl_Writer() throws Exception {
        JAXBElement<ListenerElementDeclObject> jaxbElement = getControlJAXBElement();

        Marshaller marshaller = jc.createMarshaller();
        ListenerMarshal listener = new ListenerMarshal();
        marshaller.setListener(listener);
        marshaller.marshal(jaxbElement, new StringWriter());

        testMarshalListenerElementDecl(jaxbElement, marshaller, listener);
    }

    public void testMarshalListenerElementDecl_XMLEventWriter() throws Exception {
        JAXBElement<ListenerElementDeclObject> jaxbElement = getControlJAXBElement();

        Marshaller marshaller = jc.createMarshaller();
        ListenerMarshal listener = new ListenerMarshal();
        marshaller.setListener(listener);

        XMLOutputFactory xof = XMLOutputFactory.newFactory();
        XMLEventWriter xew = xof.createXMLEventWriter(new StringWriter());
        marshaller.marshal(jaxbElement, xew);
        xew.close();

        testMarshalListenerElementDecl(jaxbElement, marshaller, listener);
    }

    public void testMarshalListenerElementDecl_XMLStreamWriter() throws Exception {
        JAXBElement<ListenerElementDeclObject> jaxbElement = getControlJAXBElement();

        Marshaller marshaller = jc.createMarshaller();
        ListenerMarshal listener = new ListenerMarshal();
        marshaller.setListener(listener);

        XMLOutputFactory xof = XMLOutputFactory.newFactory();
        XMLStreamWriter xsw = xof.createXMLStreamWriter(new StringWriter());
        marshaller.marshal(jaxbElement, xsw);
        xsw.close();

        testMarshalListenerElementDecl(jaxbElement, marshaller, listener);
    }

    private void testMarshalListenerRootObject(ListenerRootObject rootObject, Marshaller marshaller, ListenerMarshal listener) throws Exception {
        List<MarshalEvent> events = listener.getEvents();
        assertEquals(4, events.size());

        assertEquals(true, events.get(0).isBeforeEvent());
        assertSame(rootObject, events.get(0).getSource());
        assertSame(marshaller, rootObject.beforeMarshalMarshaller);

        assertEquals(true, events.get(1).isBeforeEvent());
        assertSame(rootObject.child, events.get(1).getSource());
        assertSame(marshaller, rootObject.child.beforeMarshalMarshaller);

        assertEquals(false, events.get(2).isBeforeEvent());
        assertSame(rootObject.child, events.get(2).getSource());
        assertSame(marshaller, rootObject.child.afterMarshalMarshaller);

        assertEquals(false, events.get(3).isBeforeEvent());
        assertSame(rootObject, events.get(3).getSource());
        assertSame(marshaller, rootObject.child.afterMarshalMarshaller);
    }

    public void testMarshalListenerRootObject_ContentHandler() throws Exception {
        ListenerRootObject rootObject = getControlRootObject();

        Marshaller marshaller = jc.createMarshaller();
        ListenerMarshal listener = new ListenerMarshal();
        marshaller.setListener(listener);
        marshaller.marshal(rootObject, new DefaultHandler());

        testMarshalListenerRootObject(rootObject, marshaller, listener);
    }

    public void testMarshalListenerRootObject_Node() throws Exception {
        ListenerRootObject rootObject = getControlRootObject();

        Marshaller marshaller = jc.createMarshaller();
        ListenerMarshal listener = new ListenerMarshal();
        marshaller.setListener(listener);

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.newDocument();
        marshaller.marshal(rootObject, document);

        testMarshalListenerRootObject(rootObject, marshaller, listener);
    }

    public void testMarshalListenerRootObject_OuputStream() throws Exception {
        ListenerRootObject rootObject = getControlRootObject();

        Marshaller marshaller = jc.createMarshaller();
        ListenerMarshal listener = new ListenerMarshal();
        marshaller.setListener(listener);
        marshaller.marshal(rootObject, new ByteArrayOutputStream());

        testMarshalListenerRootObject(rootObject, marshaller, listener);
    }

    public void testMarshalListenerRootObject_Writer() throws Exception {
        ListenerRootObject rootObject = getControlRootObject();

        Marshaller marshaller = jc.createMarshaller();
        ListenerMarshal listener = new ListenerMarshal();
        marshaller.setListener(listener);
        marshaller.marshal(rootObject, new StringWriter());

        testMarshalListenerRootObject(rootObject, marshaller, listener);
    }

    public void testMarshalListenerRootObject_XMLEventWriter() throws Exception {
        ListenerRootObject rootObject = getControlRootObject();

        Marshaller marshaller = jc.createMarshaller();
        ListenerMarshal listener = new ListenerMarshal();
        marshaller.setListener(listener);

        XMLOutputFactory xof = XMLOutputFactory.newFactory();
        XMLEventWriter xew = xof.createXMLEventWriter(new StringWriter());
        marshaller.marshal(rootObject, xew);
        xew.close();

        testMarshalListenerRootObject(rootObject, marshaller, listener);
    }

    public void testMarshalListenerRootObject_XMLStreamWriter() throws Exception {
        ListenerRootObject rootObject = getControlRootObject();

        Marshaller marshaller = jc.createMarshaller();
        ListenerMarshal listener = new ListenerMarshal();
        marshaller.setListener(listener);

        XMLOutputFactory xof = XMLOutputFactory.newFactory();
        XMLStreamWriter xsw = xof.createXMLStreamWriter(new StringWriter());
        marshaller.marshal(rootObject, xsw);
        xsw.close();

        testMarshalListenerRootObject(rootObject, marshaller, listener);
    }

    private void testUnmarshalListenerElementDecl(JAXBElement<ListenerElementDeclObject> jaxbElement, Unmarshaller unmarshaller, ListenerUnmarshal listener) throws Exception {
        List<UnmarshalEvent> events = listener.getEvents();
        assertEquals(6, events.size());

        assertEquals(true, events.get(0).isBeforeEvent());
        assertSame(jaxbElement, events.get(0).getTarget());
        assertNull(events.get(0).getParent());

        assertEquals(true, events.get(1).isBeforeEvent());
        assertSame(jaxbElement.getValue(), events.get(1).getTarget());
        assertSame(jaxbElement, events.get(1).getParent());
        assertSame(unmarshaller, jaxbElement.getValue().beforeUnmarshalUnmarshaller);
        assertSame(jaxbElement, jaxbElement.getValue().beforeUnmarshalParent);

        assertEquals(true, events.get(2).isBeforeEvent());
        assertSame(jaxbElement.getValue().child, events.get(2).getTarget());
        assertSame(jaxbElement.getValue(), events.get(2).getParent());
        assertSame(unmarshaller, jaxbElement.getValue().child.beforeUnmarshalUnmarshaller);
        assertSame(jaxbElement.getValue(), jaxbElement.getValue().child.beforeUnmarshalParent);

        assertEquals(false, events.get(3).isBeforeEvent());
        assertSame(jaxbElement.getValue().child, events.get(3).getTarget());
        assertSame(jaxbElement.getValue(), events.get(3).getParent());
        assertSame(unmarshaller, jaxbElement.getValue().child.afterUnmarshalUnmarshaller);
        assertSame(jaxbElement.getValue(), jaxbElement.getValue().child.afterUnmarshalParent);

        assertEquals(false, events.get(4).isBeforeEvent());
        assertSame(jaxbElement.getValue(), events.get(4).getTarget());
        assertSame(jaxbElement, events.get(4).getParent());
        assertSame(unmarshaller, jaxbElement.getValue().afterUnmarshalUnmarshaller);
        assertSame(jaxbElement, jaxbElement.getValue().afterUnmarshalParent);

        assertEquals(false, events.get(5).isBeforeEvent());
        assertSame(jaxbElement, events.get(5).getTarget());
        assertNull(events.get(5).getParent());
    }
/*
    public void testUnmarshalListenerElementDecl_InputSource() throws Exception {
        InputSource xml = new InputSource(new StringReader(ELEMENT_DECL_XML));

        Unmarshaller unmarshaller = jc.createUnmarshaller();
        ListenerUnmarshal listener = new ListenerUnmarshal();
        unmarshaller.setListener(listener);
        JAXBElement<ListenerElementDeclObject> jaxbElement = (JAXBElement<ListenerElementDeclObject>) unmarshaller.unmarshal(xml);

        testUnmarshalListenerElementDecl(jaxbElement, unmarshaller, listener);
    }

    public void testUnmarshalListenerElementDecl_Node() throws Exception {
        InputSource inputSource = new InputSource(new StringReader(ELEMENT_DECL_XML));
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(inputSource);

        Unmarshaller unmarshaller = jc.createUnmarshaller();
        ListenerUnmarshal listener = new ListenerUnmarshal();
        unmarshaller.setListener(listener);
        JAXBElement<ListenerElementDeclObject> jaxbElement = (JAXBElement<ListenerElementDeclObject>) unmarshaller.unmarshal(document);

        testUnmarshalListenerElementDecl(jaxbElement, unmarshaller, listener);
    }

    public void testUnmarshalListenerElementDecl_Reader() throws Exception {
        StringReader xml = new StringReader(ELEMENT_DECL_XML);

        Unmarshaller unmarshaller = jc.createUnmarshaller();
        ListenerUnmarshal listener = new ListenerUnmarshal();
        unmarshaller.setListener(listener);
        JAXBElement<ListenerElementDeclObject> jaxbElement = (JAXBElement<ListenerElementDeclObject>) unmarshaller.unmarshal(xml);

        testUnmarshalListenerElementDecl(jaxbElement, unmarshaller, listener);
    }

    public void testUnmarshalListenerElementDecl_Source() throws Exception {
        StringReader stringReader = new StringReader(ELEMENT_DECL_XML);
        StreamSource source = new StreamSource(stringReader);

        Unmarshaller unmarshaller = jc.createUnmarshaller();
        ListenerUnmarshal listener = new ListenerUnmarshal();
        unmarshaller.setListener(listener);
        JAXBElement<ListenerElementDeclObject> jaxbElement = (JAXBElement<ListenerElementDeclObject>) unmarshaller.unmarshal(source);

        testUnmarshalListenerElementDecl(jaxbElement, unmarshaller, listener);
    }

    public void testUnmarshalListenerElementDecl_XMLEventReader() throws Exception {
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        ListenerUnmarshal listener = new ListenerUnmarshal();
        unmarshaller.setListener(listener);

        XMLInputFactory xif = XMLInputFactory.newFactory();
        XMLEventReader xew = xif.createXMLEventReader(new StringReader(ELEMENT_DECL_XML));
        JAXBElement<ListenerElementDeclObject> jaxbElement = (JAXBElement<ListenerElementDeclObject>) unmarshaller.unmarshal(xew);

        testUnmarshalListenerElementDecl(jaxbElement, unmarshaller, listener);
    }

    public void testUnmarshalListenerElementDecl_XMLStreamReader() throws Exception {
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        ListenerUnmarshal listener = new ListenerUnmarshal();
        unmarshaller.setListener(listener);

        XMLInputFactory xif = XMLInputFactory.newFactory();
        XMLStreamReader xew = xif.createXMLStreamReader(new StringReader(ELEMENT_DECL_XML));
        JAXBElement<ListenerElementDeclObject> jaxbElement = (JAXBElement<ListenerElementDeclObject>) unmarshaller.unmarshal(xew);

        testUnmarshalListenerElementDecl(jaxbElement, unmarshaller, listener);
    }
*/
    private void testUnmarshalListenerRootObject(ListenerRootObject rootObject, Unmarshaller unmarshaller, ListenerUnmarshal listener) throws Exception {
        List<UnmarshalEvent> events = listener.getEvents();
        assertEquals(4, events.size());

        assertEquals(true, events.get(0).isBeforeEvent());
        assertSame(rootObject, events.get(0).getTarget());
        assertNull(events.get(0).getParent());
        assertSame(unmarshaller, rootObject.beforeUnmarshalUnmarshaller);
        assertNull(rootObject.beforeUnmarshalParent);

        assertEquals(true, events.get(1).isBeforeEvent());
        assertSame(rootObject.child, events.get(1).getTarget());
        assertSame(rootObject, events.get(1).getParent());
        assertSame(unmarshaller, rootObject.child.beforeUnmarshalUnmarshaller);
        assertSame(rootObject, rootObject.child.beforeUnmarshalParent);

        assertEquals(false, events.get(2).isBeforeEvent());
        assertSame(rootObject.child, events.get(2).getTarget());
        assertSame(rootObject, events.get(2).getParent());
        assertSame(unmarshaller, rootObject.child.afterUnmarshalUnmarshaller);
        assertSame(rootObject, rootObject.child.afterUnmarshalParent);

        assertEquals(false, events.get(3).isBeforeEvent());
        assertSame(rootObject, events.get(3).getTarget());
        assertNull(events.get(3).getParent());
        assertSame(unmarshaller, rootObject.afterUnmarshalUnmarshaller);
        assertNull(rootObject.afterUnmarshalParent);
    }

    public void testUnmarshalListenerRootObject_InputSource() throws Exception {
        InputSource xml = new InputSource(new StringReader(ROOT_XML));

        Unmarshaller unmarshaller = jc.createUnmarshaller();
        ListenerUnmarshal listener = new ListenerUnmarshal();
        unmarshaller.setListener(listener);
        ListenerRootObject rootObject = (ListenerRootObject) unmarshaller.unmarshal(xml);

        testUnmarshalListenerRootObject(rootObject, unmarshaller, listener);
    }

    public void testUnmarshalListenerRootObject_Node() throws Exception {
        InputSource inputSource = new InputSource(new StringReader(ROOT_XML));
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(inputSource);

        Unmarshaller unmarshaller = jc.createUnmarshaller();
        ListenerUnmarshal listener = new ListenerUnmarshal();
        unmarshaller.setListener(listener);
        ListenerRootObject rootObject = (ListenerRootObject) unmarshaller.unmarshal(document);

        testUnmarshalListenerRootObject(rootObject, unmarshaller, listener);
    }

    public void testUnmarshalListenerRootObject_Reader() throws Exception {
        StringReader xml = new StringReader(ROOT_XML);

        Unmarshaller unmarshaller = jc.createUnmarshaller();
        ListenerUnmarshal listener = new ListenerUnmarshal();
        unmarshaller.setListener(listener);
        ListenerRootObject rootObject = (ListenerRootObject) unmarshaller.unmarshal(xml);

        testUnmarshalListenerRootObject(rootObject, unmarshaller, listener);
    }

    public void testUnmarshalListenerRootObject_Source() throws Exception {
        StreamSource source = new StreamSource(new StringReader(ROOT_XML));

        Unmarshaller unmarshaller = jc.createUnmarshaller();
        ListenerUnmarshal listener = new ListenerUnmarshal();
        unmarshaller.setListener(listener);
        ListenerRootObject rootObject = (ListenerRootObject) unmarshaller.unmarshal(source);

        testUnmarshalListenerRootObject(rootObject, unmarshaller, listener);
    }

    public void testUnmarshalListenerRootObject_XMLEventReader() throws Exception {
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        ListenerUnmarshal listener = new ListenerUnmarshal();
        unmarshaller.setListener(listener);

        XMLInputFactory xif = XMLInputFactory.newFactory();
        XMLEventReader xew = xif.createXMLEventReader(new StringReader(ROOT_XML));
        ListenerRootObject rootObject = (ListenerRootObject) unmarshaller.unmarshal(xew);

        testUnmarshalListenerRootObject(rootObject, unmarshaller, listener);
    }

    public void testUnmarshalListenerRootObject_XMLStreamReader() throws Exception {
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        ListenerUnmarshal listener = new ListenerUnmarshal();
        unmarshaller.setListener(listener);

        XMLInputFactory xif = XMLInputFactory.newFactory();
        XMLStreamReader xsw = xif.createXMLStreamReader(new StringReader(ROOT_XML));
        ListenerRootObject rootObject = (ListenerRootObject) unmarshaller.unmarshal(xsw);

        testUnmarshalListenerRootObject(rootObject, unmarshaller, listener);
    }

    private void testUnmarshalListenerRootObjectAsJAXBElement(JAXBElement<ListenerRootObject> jaxbElement, Unmarshaller unmarshaller, ListenerUnmarshal listener) throws Exception {
        List<UnmarshalEvent> events = listener.getEvents();
        assertEquals(6, events.size());

        assertEquals(true, events.get(0).isBeforeEvent());
        assertSame(jaxbElement, events.get(0).getTarget());
        assertNull(events.get(0).getParent());

        assertEquals(true, events.get(1).isBeforeEvent());
        assertSame(jaxbElement.getValue(), events.get(1).getTarget());
        assertSame(jaxbElement, events.get(1).getParent());
        assertSame(unmarshaller, jaxbElement.getValue().beforeUnmarshalUnmarshaller);
        assertSame(jaxbElement, jaxbElement.getValue().beforeUnmarshalParent);

        assertEquals(true, events.get(2).isBeforeEvent());
        assertSame(jaxbElement.getValue().child, events.get(2).getTarget());
        assertSame(jaxbElement.getValue(), events.get(2).getParent());
        assertSame(unmarshaller, jaxbElement.getValue().child.beforeUnmarshalUnmarshaller);
        assertSame(jaxbElement.getValue(), jaxbElement.getValue().child.beforeUnmarshalParent);

        assertEquals(false, events.get(3).isBeforeEvent());
        assertSame(jaxbElement.getValue().child, events.get(3).getTarget());
        assertSame(jaxbElement.getValue(), events.get(3).getParent());
        assertSame(unmarshaller, jaxbElement.getValue().child.afterUnmarshalUnmarshaller);
        assertSame(jaxbElement.getValue(), jaxbElement.getValue().child.afterUnmarshalParent);

        assertEquals(false, events.get(4).isBeforeEvent());
        assertSame(jaxbElement.getValue(), events.get(4).getTarget());
        assertSame(jaxbElement, events.get(4).getParent());
        assertSame(unmarshaller, jaxbElement.getValue().afterUnmarshalUnmarshaller);
        assertSame(jaxbElement, jaxbElement.getValue().afterUnmarshalParent);

        assertEquals(false, events.get(5).isBeforeEvent());
        assertSame(jaxbElement, events.get(5).getTarget());
        assertNull(events.get(5).getParent());
    }
/*
    public void testUnmarshalListenerRootObjectAsJAXBElement_Source() throws Exception {
        StringReader xml = new StringReader(ROOT_XML);

        Unmarshaller unmarshaller = jc.createUnmarshaller();
        ListenerUnmarshal listener = new ListenerUnmarshal();
        unmarshaller.setListener(listener);
        JAXBElement<ListenerRootObject> jaxbElement = unmarshaller.unmarshal(new StreamSource(xml), ListenerRootObject.class);

        testUnmarshalListenerRootObjectAsJAXBElement(jaxbElement, unmarshaller, listener);
    }

    public void testUnmarshalListenerRootObjectAsJAXBElement_XMLEventReader() throws Exception {
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        ListenerUnmarshal listener = new ListenerUnmarshal();
        unmarshaller.setListener(listener);

        XMLInputFactory xif = XMLInputFactory.newFactory();
        XMLEventReader xer = xif.createXMLEventReader(new StringReader(ROOT_XML));
        JAXBElement<ListenerRootObject> jaxbElement = unmarshaller.unmarshal(xer, ListenerRootObject.class);

        testUnmarshalListenerRootObjectAsJAXBElement(jaxbElement, unmarshaller, listener);
    }

    public void testUnmarshalListenerRootObjectAsJAXBElement_XMLStreamReader() throws Exception {
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        ListenerUnmarshal listener = new ListenerUnmarshal();
        unmarshaller.setListener(listener);

        XMLInputFactory xif = XMLInputFactory.newFactory();
        XMLStreamReader xsr = xif.createXMLStreamReader(new StringReader(ROOT_XML));
        JAXBElement<ListenerRootObject> jaxbElement = unmarshaller.unmarshal(xsr, ListenerRootObject.class);

        testUnmarshalListenerRootObjectAsJAXBElement(jaxbElement, unmarshaller, listener);
    }
*/

    public void testClassCallbacksForBinder() throws Exception {
        Binder binder = jc.createBinder();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new InputSource(new StringReader(ROOT_XML)));

        //marshal and unmarshal
        ListenerRootObject root = (ListenerRootObject) binder.unmarshal(doc);
        binder.marshal(root, db.newDocument());

        // just tests the fact that they are called
        assertNotNull(root.afterMarshalMarshaller);
        assertNotNull(root.beforeMarshalMarshaller);
        assertNotNull(root.afterUnmarshalUnmarshaller);
        assertNotNull(root.beforeUnmarshalUnmarshaller);
    }
}
