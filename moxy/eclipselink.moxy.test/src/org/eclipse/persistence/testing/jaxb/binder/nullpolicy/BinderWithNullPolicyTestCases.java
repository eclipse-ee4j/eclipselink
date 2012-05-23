/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Matt MacIvor - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.binder.nullpolicy;

import java.io.File;
import java.io.StringReader;

import javax.xml.bind.Binder;
import javax.xml.bind.JAXBContext;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.testing.jaxb.JAXBXMLComparer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import junit.framework.TestCase;

public class BinderWithNullPolicyTestCases extends TestCase{

    private XMLParser parser;

    public BinderWithNullPolicyTestCases() {
        XMLPlatform platform = XMLPlatformFactory.getInstance().getXMLPlatform();
        parser = platform.newXMLParser();
    }

    public void testAbsentNode() throws Exception {
        String xml = "<employee><!-- Comment 1 --><name>Matt</name><age>32</age><!-- Comment 2 --><address>Kanata</address></employee>";
        String controlSource = "org/eclipse/persistence/testing/jaxb/binder/nullpolicy/absentnode.xml";
        Document controlDocument = parser.parse(new File(controlSource));
        
        JAXBContext ctx = JAXBContextFactory.createContext(new Class[]{EmployeeA.class}, null);
        
        Binder binder = ctx.createBinder();
        
        EmployeeA emp = (EmployeeA)binder.unmarshal(parser.parse(new StringReader(xml)));
        
        emp.setName(null);
        
        binder.updateXML(emp);
        
        JAXBXMLComparer comparer = new JAXBXMLComparer();
        assertTrue("Marshalled document does not match the control document.", comparer.isNodeEqual(controlDocument, ((Node)binder.getXMLNode(emp)).getOwnerDocument()));
    }
    
    public void testXsiNilNullMarshal() throws Exception {
        String xml = "<employee><!-- Comment 1 --><name>Matt</name><age>32</age><!-- Comment 2 --><address>Kanata</address></employee>";
        String controlSource = "org/eclipse/persistence/testing/jaxb/binder/nullpolicy/xsinil.xml";
        Document controlDocument = parser.parse(new File(controlSource));
        
        JAXBContext ctx = JAXBContextFactory.createContext(new Class[]{EmployeeB.class}, null);
        
        Binder binder = ctx.createBinder();
        
        EmployeeB emp = (EmployeeB)binder.unmarshal(parser.parse(new StringReader(xml)));
        
        emp.setName(null);
        
        binder.updateXML(emp);
        
        JAXBXMLComparer comparer = new JAXBXMLComparer();
        assertTrue("Marshalled document does not match the control document.", comparer.isNodeEqual(controlDocument, ((Node)binder.getXMLNode(emp)).getOwnerDocument()));
    }
    
    public void testXsiNilUnmarshalMarshalValue() throws Exception {
        String xml = "<employee><!-- Comment 1 --><name xmlns:xsi=\"" + XMLConstants.SCHEMA_INSTANCE_URL + "\" xsi:nil=\"true\">Matt</name><age>32</age><!-- Comment 2 --><address>Kanata</address></employee>";
        String controlSource = "org/eclipse/persistence/testing/jaxb/binder/nullpolicy/nilwithvalue.xml";
        Document controlDocument = parser.parse(new File(controlSource));
        
        JAXBContext ctx = JAXBContextFactory.createContext(new Class[]{EmployeeB.class}, null);
        
        Binder binder = ctx.createBinder();
        
        EmployeeB emp = (EmployeeB)binder.unmarshal(parser.parse(new StringReader(xml)));
        
        emp.setName("Matt");
        
        binder.updateXML(emp);
        
        JAXBXMLComparer comparer = new JAXBXMLComparer();
        assertTrue("Marshalled document does not match the control document.", comparer.isNodeEqual(controlDocument, ((Node)binder.getXMLNode(emp)).getOwnerDocument()));
    }
    
    public void testEmptyNode() throws Exception {
        String xml = "<employee><!-- Comment 1 --><name>Matt</name><age>32</age><!-- Comment 2 --><address>Kanata</address></employee>";
        String controlSource = "org/eclipse/persistence/testing/jaxb/binder/nullpolicy/emptynode.xml";
        Document controlDocument = parser.parse(new File(controlSource));
        
        JAXBContext ctx = JAXBContextFactory.createContext(new Class[]{EmployeeC.class}, null);
        
        Binder binder = ctx.createBinder();
        
        EmployeeC emp = (EmployeeC)binder.unmarshal(parser.parse(new StringReader(xml)));
        
        emp.setName(null);
        
        binder.updateXML(emp);
        
        JAXBXMLComparer comparer = new JAXBXMLComparer();
        assertTrue("Marshalled document does not match the control document.", comparer.isNodeEqual(controlDocument, ((Node)binder.getXMLNode(emp)).getOwnerDocument()));
    }
}
