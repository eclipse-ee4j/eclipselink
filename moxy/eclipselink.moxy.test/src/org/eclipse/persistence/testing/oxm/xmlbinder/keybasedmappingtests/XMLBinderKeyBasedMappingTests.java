/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.xmlbinder.keybasedmappingtests;

import java.io.*;
import java.util.ArrayList;
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
 *  @version $Header: BasicDocumentPreservationTestCases.java 06-jun-2005.08:50:20 dmahar Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */
public class XMLBinderKeyBasedMappingTests extends OXTestCase {
    public XMLContext context;
    public XMLMarshaller marshaller;
    public XMLUnmarshaller unmarshaller;
    public XMLBinder binder;
    public DocumentBuilder parser;

    public XMLBinderKeyBasedMappingTests() {
        super("XMLBinder w/ Key Based mappings tests");
    }

    public XMLBinderKeyBasedMappingTests(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        context = this.getXMLContext(new SingleElementKeyProject());
        marshaller = context.createMarshaller();
        unmarshaller = context.createUnmarshaller();
        binder = context.createBinder();
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        builderFactory.setIgnoringElementContentWhitespace(false);
        parser = builderFactory.newDocumentBuilder();
        super.useLogging = true;
    }

    public void testUpdateXMLValue() throws Exception {
        Document sourceDocument = parse("org/eclipse/persistence/testing/oxm/xmlbinder/keybasedmappingtests/updatevalue_before.xml");
        Document controlDocument = parse("org/eclipse/persistence/testing/oxm/xmlbinder/keybasedmappingtests/updatevalue_after.xml");
        
        //Switch the employee's Address so the keys get updated
        Root root = (Root)binder.unmarshal(sourceDocument);
        Employee emp = root.employee;
        emp.address = (Address)root.addresses.toArray()[1];
        
        binder.updateXML(emp);
        assertXMLIdentical(controlDocument, binder.getXMLNode(root).getOwnerDocument());
    }

    public void testUpdateXMLCollection() throws Exception {
        
        Document sourceDocument = parse("org/eclipse/persistence/testing/oxm/xmlbinder/keybasedmappingtests/updatecollection_before.xml");
        Document controlDocument = parse("org/eclipse/persistence/testing/oxm/xmlbinder/keybasedmappingtests/updatecollection_after.xml");
        
        Root root = (Root)binder.unmarshal(sourceDocument);
        //Swap the order of addresses.
        Vector newAddrs = new Vector(root.addresses.size());
        Object[] oldAddrs = root.addresses.toArray();
        for(int i = 0; i < oldAddrs.length; i++) {
            newAddrs.add(oldAddrs[oldAddrs.length - (i + 1)]);
        }
        root.addresses = newAddrs;
        binder.updateXML(root);
        assertXMLIdentical(controlDocument, binder.getXMLNode(root).getOwnerDocument());
    }
    
    public void testNullAddress() throws Exception {
        //Read in an employee with a null address, set the address and verify 
        //The keys are written out properly.
        Document sourceDocument = parse("org/eclipse/persistence/testing/oxm/xmlbinder/keybasedmappingtests/nulladdress_before.xml");
        Document controlDocument = parse("org/eclipse/persistence/testing/oxm/xmlbinder/keybasedmappingtests/nulladdress_after.xml");
        
        Root root = (Root)binder.unmarshal(sourceDocument);
        root.employee.address = (Address)root.addresses.toArray()[0];
        binder.updateXML(root.employee);
        
        assertXMLIdentical(controlDocument, binder.getXMLNode(root).getOwnerDocument());
    }
    public void testUpdateObject() throws Exception {
        Document sourceDocument = parse("org/eclipse/persistence/testing/oxm/xmlbinder/keybasedmappingtests/updateobject.xml");
        Employee controlemp = getControlEmployee();

        Root root = (Root)binder.unmarshal(sourceDocument);
        Employee emp = root.employee;
        
        //change the employee's addr id in the doc and then update to ensure the correct
        //address is present.
        Node employeeNode = binder.getXMLNode(emp);
        
        NodeList addressIds = ((Element)employeeNode).getElementsByTagName("address-id");
        addressIds.item(0).getFirstChild().setNodeValue("11199");
        log("Employee:" + emp);
        log("Address:" + emp.address);
        binder.updateObject(binder.getXMLNode(root));
        log("Employee:" + emp);
        log("Address:" + emp.address);
        
        assertTrue(emp.address.equals(controlemp.address));

    }
    
    public Employee getControlEmployee() {
        Employee emp = new Employee();
        emp.id = "222";
        emp.name = "Joe Smith";
        emp.address = getControlAddress();
        return emp;
    }
    
    public Address getControlAddress() {
        Address addr = new Address();
        addr.id = "11199";
        addr.city = "Anytown";
        addr.street = "Another St.";
        addr.country = "Canada";
        addr.zip = "Y0Y0Y0";
        return addr;
    }
    
    private Document parse(String resource) throws Exception {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(resource);
        Document document = parser.parse(stream);
        removeEmptyTextNodes(document);
        return document;
    }
}


