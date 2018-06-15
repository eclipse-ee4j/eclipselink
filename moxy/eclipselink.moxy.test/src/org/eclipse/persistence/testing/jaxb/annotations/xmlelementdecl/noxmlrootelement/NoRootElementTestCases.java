/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith - January 2013
package org.eclipse.persistence.testing.jaxb.annotations.xmlelementdecl.noxmlrootelement;

import java.io.InputStream;
import java.io.StringReader;

import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public class NoRootElementTestCases extends JAXBWithJSONTestCases{

    private static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlelementdecl/norootelement.xml";
    private static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlelementdecl/norootelement.json";

    public NoRootElementTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[] {Foo.class, ObjectFactory.class});
        jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
    }

    @Override
    protected Foo getControlObject() {
        Foo f = new Foo();
        f.theValue = 10;
        f.theOtherValue = 20;
        return f;
    }

    public void setupControlDocs() throws Exception{

        Document doc = parser.newDocument();
        Element root = doc.createElement("DUMMYROOT");
        doc.appendChild(root);

        Element child1 = doc.createElement("theValue");
        child1.setTextContent("10");
        Element child2 = doc.createElement("theOtherValue");
        child2.setTextContent("20");
        root.appendChild(child1);
        root.appendChild(child2);
        controlDocument = doc;

    }
    public Document getTestDocument(InputStream is) throws Exception{

        byte[] bytes = new byte[is.available()];
        is.read(bytes);

        String s = new String(bytes);
        s = "<DUMMYROOT>" + s + "</DUMMYROOT>";
        return parser.parse(new InputSource(new StringReader(s)));
     }

     public Document getTestDocument(String s) throws Exception{
         s = "<DUMMYROOT>" + s + "</DUMMYROOT>";
         return parser.parse(new InputSource(new StringReader(s)));
     }

    @Override
    public boolean isUnmarshalTest(){
        return false;
    }

    public void testObjectToContentHandler() throws Exception{
        try{
            super.testObjectToContentHandler();
        }catch(MarshalException e){
            return;
        }
        fail("an error was expected");
    }

    public void testObjectToXMLDocument() throws Exception{
        try{
            super.testObjectToXMLDocument();
        }catch(MarshalException e){
              assertTrue(((XMLMarshalException)e.getLinkedException()).getErrorCode() == 25003);
            return;
        }
        fail("an error was expected");
    }

    @Override
    public void testObjectToXMLStreamWriter() throws Exception {
        return;
        /*
        try {
            super.testObjectToXMLStreamWriter();
        } catch (Exception e) {
            return;
        }
        fail("an error was expected");
        */
    }

    @Override
    public void testObjectToXMLStreamWriterRecord() throws Exception {
        return;
        /*
        try {
            super.testObjectToXMLStreamWriterRecord();
        } catch (Exception e) {
            return;
        }
        fail("an error was expected");
        */
    }

    @Override
    public void testObjectToXMLEventWriter() throws Exception {
        return;
        /*
        try {
            super.testObjectToXMLEventWriter();
        } catch (Exception e) {
            return;
        }
        fail("an error was expected");
        */
    }

}
