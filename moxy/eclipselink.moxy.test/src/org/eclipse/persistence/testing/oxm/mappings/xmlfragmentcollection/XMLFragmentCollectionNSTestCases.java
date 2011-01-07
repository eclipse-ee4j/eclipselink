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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.xmlfragmentcollection;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.platform.xml.SAXDocumentBuilder;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Namespace qualified XMLFragmentMapping tests
 */
public class XMLFragmentCollectionNSTestCases extends XMLMappingTestCases {
    public XMLFragmentCollectionNSTestCases(String name) throws Exception {
        super(name);
    }
    
    /**
     * Should be overridden in subclasses
     */
    protected Object getControlObject() {
        return null;
    }

    /**
     * Use the transform API to ensure that any required local namespace
     * declarations are set properly
     */
    protected Document importNodeFix(Document testDocument) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(testDocument), new StreamResult(writer));
            StringReader reader = new StringReader(writer.toString());
            testDocument = parser.parse(new InputSource(reader));
            writer.close();
            reader.close();
        } catch (Exception x) {}
        
        return testDocument;
    }
    
    public void testObjectToXMLDocument() throws Exception {
        Object objectToWrite = getWriteControlObject();
        XMLDescriptor desc = null;
        if (objectToWrite instanceof XMLRoot) {
            desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(((XMLRoot)objectToWrite).getObject().getClass());
        } else {
            desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(objectToWrite.getClass());
        }
        int sizeBefore = getNamespaceResolverSize(desc);
        Document testDocument = xmlMarshaller.objectToXML(objectToWrite);
        int sizeAfter = getNamespaceResolverSize(desc);
        assertEquals(sizeBefore, sizeAfter);
        
        Document docToCompare = importNodeFix(testDocument);
        log("**objectToXMLDocumentTest**");
        log("Expected:");
        log(getWriteControlDocument());
        log("\nActual:");
        log(docToCompare);
        assertXMLIdentical(getWriteControlDocument(), docToCompare);
    }

    public void testObjectToContentHandler() throws Exception {
        SAXDocumentBuilder builder = new SAXDocumentBuilder();
        Object objectToWrite = getWriteControlObject();
        XMLDescriptor desc = null;
        if (objectToWrite instanceof XMLRoot) {
            desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(((XMLRoot)objectToWrite).getObject().getClass());
        } else {
            desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(objectToWrite.getClass());
        }
        int sizeBefore = getNamespaceResolverSize(desc);
        xmlMarshaller.marshal(objectToWrite, builder);
        int sizeAfter = getNamespaceResolverSize(desc);
        assertEquals(sizeBefore, sizeAfter);

        Document controlDocument = getWriteControlDocument();
        Document testDocument = importNodeFix(builder.getDocument());
        log("**testObjectToContentHandler**");
        log("Expected:");
        log(controlDocument);
        log("\nActual:");
        log(testDocument);
        assertXMLIdentical(controlDocument, testDocument);
    }
}
