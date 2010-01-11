/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
public class XMLBinderAnyObjectTestCases extends OXTestCase {
    public XMLContext context;
    public XMLMarshaller marshaller;
    public XMLUnmarshaller unmarshaller;
    public XMLBinder binder;
    public DocumentBuilder parser;

    public XMLBinderAnyObjectTestCases() {
        super("XMLBinder w/ AnyObject mapping test cases");
    }

    public XMLBinderAnyObjectTestCases(String name) {
        super(name);
        super.useLogging = true;
    }

    public void setUp() throws Exception {
        context = this.getXMLContext(new AnyObjectWithoutGroupingElementProject());
        marshaller = context.createMarshaller();
        unmarshaller = context.createUnmarshaller();
        binder = context.createBinder();
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setIgnoringElementContentWhitespace(true);
        parser = builderFactory.newDocumentBuilder();
    }

    public void testUpdateXMLValue() throws Exception {
        Document sourceDocument = parse("org/eclipse/persistence/testing/oxm/xmlbinder/anymappingtests/anyobjectupdatevalue_before.xml");
        Document controlDocument = parse("org/eclipse/persistence/testing/oxm/xmlbinder/anymappingtests/anyobjectupdatevalue_after.xml");
        
        AnyObjectRoot root = (AnyObjectRoot)binder.unmarshal(sourceDocument);
        AnyObjectChild child = (AnyObjectChild)root.getAny();
        child.setContent("New Content!!");
        
        binder.updateXML(child);
        assertXMLIdentical(controlDocument, binder.getXMLNode(root).getOwnerDocument());
    }

    public void testUpdateXMLDirectValue() throws Exception {
        Document sourceDocument = parse("org/eclipse/persistence/testing/oxm/xmlbinder/anymappingtests/anyobjectupdatedirect_before.xml");
        Document controlDocument = parse("org/eclipse/persistence/testing/oxm/xmlbinder/anymappingtests/anyobjectupdatedirect_after.xml");
        
        AnyObjectRoot root = (AnyObjectRoot)binder.unmarshal(sourceDocument);
        root.setAny("A String");
        //children.remove(1);
        
        binder.updateXML(root);
        assertXMLIdentical(controlDocument, binder.getXMLNode(root).getOwnerDocument());
    }
    

    
    public void testUpdateObjectChild() throws Exception {
        Document sourceDocument = parse("org/eclipse/persistence/testing/oxm/xmlbinder/anymappingtests/anyobjectupdateobject.xml");
        AnyObjectChild controlChild = getControlChild();

        AnyObjectRoot root = (AnyObjectRoot)binder.unmarshal(sourceDocument);
        AnyObjectChild child = (AnyObjectChild)root.getAny();
        
        NodeList nodes = sourceDocument.getDocumentElement().getElementsByTagName("child");
        nodes.item(0).getFirstChild().setNodeValue("Changed 1");
        
        binder.updateObject(nodes.item(0));
        
        assertTrue(child.equals(controlChild));

    }
    
    private AnyObjectChild getControlChild() {
        AnyObjectChild child = new AnyObjectChild();
        child.setContent("Changed 1");
        return child;
    }
    
    private Document parse(String resource) throws Exception {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(resource);
        Document document = parser.parse(stream);
        removeEmptyTextNodes(document);
        return document;
    }
}
