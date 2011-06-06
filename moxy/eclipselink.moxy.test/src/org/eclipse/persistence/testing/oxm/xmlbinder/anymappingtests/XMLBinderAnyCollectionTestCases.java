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
package org.eclipse.persistence.testing.oxm.xmlbinder.anymappingtests;

import java.io.*;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.testing.oxm.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *  @version $Header: XMLBinderAnyCollectionTestCases.java 05-jul-2007.12:55:58 mmacivor Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */
public class XMLBinderAnyCollectionTestCases extends OXTestCase {
    public XMLContext context;
    public XMLMarshaller marshaller;
    public XMLUnmarshaller unmarshaller;
    public XMLBinder binder;
    public DocumentBuilder parser;

    public XMLBinderAnyCollectionTestCases() {
        super("Basic Document Preservation Tests");
    }

    public XMLBinderAnyCollectionTestCases(String name) {
        super(name);
        super.useLogging = true;
    }

    public void setUp() throws Exception {
        context = this.getXMLContext(new AnyCollectionWithGroupingElementProject());
        marshaller = context.createMarshaller();
        unmarshaller = context.createUnmarshaller();
        binder = context.createBinder();
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setIgnoringElementContentWhitespace(true);
        parser = builderFactory.newDocumentBuilder();
    }

    public void testUpdateXMLValue() throws Exception {
        Document sourceDocument = parse("org/eclipse/persistence/testing/oxm/xmlbinder/anymappingtests/anycollectionupdatevalue_before.xml");
        Document controlDocument = parse("org/eclipse/persistence/testing/oxm/xmlbinder/anymappingtests/anycollectionupdatevalue_after.xml");
        
        AnyCollectionRoot root = (AnyCollectionRoot)binder.unmarshal(sourceDocument);
        AnyCollectionChild child = (AnyCollectionChild)root.getAny().firstElement();
        child.setContent("New Content!!");
        
        binder.updateXML(child);
        assertXMLIdentical(controlDocument, binder.getXMLNode(root).getOwnerDocument());
    }

    public void testUpdateXMLCollection() throws Exception {
        Document sourceDocument = parse("org/eclipse/persistence/testing/oxm/xmlbinder/anymappingtests/updatecollection_before.xml");
        Document controlDocument = parse("org/eclipse/persistence/testing/oxm/xmlbinder/anymappingtests/updatecollection_after.xml");
        
        AnyCollectionRoot root = (AnyCollectionRoot)binder.unmarshal(sourceDocument);
        
        Vector children = root.getAny();
        //children.remove(1);
        AnyCollectionChild newChild = new AnyCollectionChild();
        newChild.setContent("newChild1");
        children.add(0, newChild);
        
        binder.updateXML(root);
        assertXMLIdentical(controlDocument, binder.getXMLNode(root).getOwnerDocument());
    }
    
/*
    public void testUpdateObjectRoot() throws Exception {
        Document sourceDocument = parse("org/eclipse/persistence/testing/oxm/xmlbinder/anymappingtests/anycollectionupdateobject.xml");
        AnyCollectionRoot controlRoot = getControlRoot();
        
        AnyCollectionRoot root = (AnyCollectionRoot)binder.unmarshal(sourceDocument);
        
        Element elem = sourceDocument.createElement("child");
        Node text = sourceDocument.createTextNode("New Child");
        elem.appendChild(text);
        sourceDocument.getDocumentElement().appendChild(elem);
        binder.updateObject(binder.getXMLNode(root));
        
        assertTrue(root.equals(controlRoot));
    }
*/
    public void testUpdateObjectChild() throws Exception {
        Document sourceDocument = parse("org/eclipse/persistence/testing/oxm/xmlbinder/anymappingtests/anycollectionupdateobject.xml");
        AnyCollectionChild controlChild = getControlChild();

        AnyCollectionRoot root = (AnyCollectionRoot)binder.unmarshal(sourceDocument);
        AnyCollectionChild child = (AnyCollectionChild)root.getAny().firstElement();
        
        NodeList nodes = sourceDocument.getDocumentElement().getElementsByTagName("child");
        nodes.item(0).getFirstChild().setNodeValue("Changed 1");
        
        binder.updateObject(nodes.item(0));
        
        assertTrue(child.equals(controlChild));

    }
    
    private AnyCollectionChild getControlChild() {
        AnyCollectionChild child = new AnyCollectionChild();
        child.setContent("Changed 1");
        return child;
    }
    
    private AnyCollectionRoot getControlRoot() {
        AnyCollectionRoot root = new AnyCollectionRoot();
        root.setAny(new Vector());
        AnyCollectionChild child = new AnyCollectionChild();
        child.setContent("Child1");
        root.getAny().add(child);
        child = new AnyCollectionChild();
        child.setContent("Child2");
        root.getAny().add(child);
        child = new AnyCollectionChild();
        child.setContent("New Child");
        root.getAny().add(child);
        
        return root;
    }
    private Document parse(String resource) throws Exception {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(resource);
        Document document = parser.parse(stream);
        removeEmptyTextNodes(document);
        return document;
    }
}
