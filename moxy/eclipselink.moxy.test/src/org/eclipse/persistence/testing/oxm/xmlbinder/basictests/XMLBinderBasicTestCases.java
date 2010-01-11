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
package org.eclipse.persistence.testing.oxm.xmlbinder.basictests;

import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.testing.oxm.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 *  @version $Header: BasicDocumentPreservationTestCases.java 06-jun-2005.08:50:20 dmahar Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */
public class XMLBinderBasicTestCases extends OXTestCase {
    public XMLContext context;
    public XMLMarshaller marshaller;
    public XMLUnmarshaller unmarshaller;
    public XMLBinder binder;
    public DocumentBuilder parser;

    public XMLBinderBasicTestCases() {
        super("Basic Document Preservation Tests");
    }

    public XMLBinderBasicTestCases(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        context = this.getXMLContext(new BasicBinderTestsProject());
        marshaller = context.createMarshaller();
        unmarshaller = context.createUnmarshaller();
        binder = context.createBinder();
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setIgnoringElementContentWhitespace(true);
        builderFactory.setNamespaceAware(true);
        parser = builderFactory.newDocumentBuilder();
    }

    public void testUpdateXMLValue() throws Exception {
        Document sourceDocument = parse("org/eclipse/persistence/testing/oxm/xmlbinder/basictests/updatevalue_before.xml");
        Document controlDocument = parse("org/eclipse/persistence/testing/oxm/xmlbinder/basictests/updatevalue_after.xml");
        
        Employee emp = (Employee)binder.unmarshal(sourceDocument);
        emp.setFirstName("Bob");
        emp.setLastName("Jones");
        
        binder.updateXML(emp);
        
        assertXMLIdentical(controlDocument, binder.getXMLNode(emp).getOwnerDocument());
    }

    public void testUpdateXMLCollection() throws Exception {
        Document sourceDocument = parse("org/eclipse/persistence/testing/oxm/xmlbinder/basictests/updatecollection_before.xml");
        Document controlDocument = parse("org/eclipse/persistence/testing/oxm/xmlbinder/basictests/updatecollection_after.xml");
        
        Employee emp = (Employee)binder.unmarshal(sourceDocument);
        
        emp.getPhoneNumbers().remove(1);
        emp.addResponsibility("New Responsibility");
        
        binder.updateXML(emp);
        
        assertXMLIdentical(controlDocument, binder.getXMLNode(emp).getOwnerDocument());
    }
    
    public void testUpdateSpacedOutXMLCollection() throws Exception {
        Document sourceDocument = parse("org/eclipse/persistence/testing/oxm/xmlbinder/basictests/updatespacedoutcollection_before.xml");
        Document controlDocument = parse("org/eclipse/persistence/testing/oxm/xmlbinder/basictests/updatespacedoutcollection_after.xml");
        
        Employee emp = (Employee)binder.unmarshal(sourceDocument);
        
        emp.addResponsibility("New Responsibility");
        
        binder.updateXML(emp);
        
        assertXMLIdentical(controlDocument, binder.getXMLNode(emp).getOwnerDocument());
    }
    
    public void testUpdateObjectRoot() throws Exception {
        Document sourceDocument = parse("org/eclipse/persistence/testing/oxm/xmlbinder/basictests/updateobject.xml");
        Employee controlEmp = getControlEmployee();
        
        Employee emp = (Employee)binder.unmarshal(sourceDocument);
        
        sourceDocument.getDocumentElement().getChildNodes().item(1).getFirstChild().setNodeValue("Bob");
        sourceDocument.getDocumentElement().getChildNodes().item(2).getFirstChild().setNodeValue("Jones");
        
        binder.updateObject(binder.getXMLNode(emp));
        
        assertTrue(emp.getFirstName().equals(controlEmp.getFirstName()) && emp.getLastName().equals(controlEmp.getLastName()));
    }
    
    public void testUpdateObjectChild() throws Exception {
        Document sourceDocument = parse("org/eclipse/persistence/testing/oxm/xmlbinder/basictests/updateobject.xml");
        Address controlAddr = getControlAddress();

        Employee emp = (Employee)binder.unmarshal(sourceDocument);
        
        NodeList nodes = sourceDocument.getDocumentElement().getElementsByTagNameNS("http://www.myns.com", "address");
        nodes.item(0).getChildNodes().item(1).getFirstChild().setNodeValue("45 O'Connor");
        nodes.item(0).getChildNodes().item(2).getFirstChild().setNodeValue("Ottawa");
        
        binder.updateObject(nodes.item(0));
        
        Address addr = emp.getAddress();
        assertTrue(addr.getStreet().equals(controlAddr.getStreet()) && addr.getCity().equals(controlAddr.getCity()));

    }
    
    public Employee getControlEmployee() {
        Employee emp = new Employee();
        emp.setFirstName("Bob");
        emp.setLastName("Jones");
        return emp;
    }
    
    public Address getControlAddress() {
        Address addr = new Address();
        addr.setStreet("45 O'Connor");
        addr.setCity("Ottawa");
        return addr;
    }
    
    private Document parse(String resource) throws Exception {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(resource);
        Document document = parser.parse(stream);
        removeEmptyTextNodes(document);
        return document;
    }
}

