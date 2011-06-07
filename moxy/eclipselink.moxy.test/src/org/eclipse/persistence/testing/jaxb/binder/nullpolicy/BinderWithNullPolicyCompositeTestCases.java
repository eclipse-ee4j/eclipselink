/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Matt MacIvor - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.binder.nullpolicy;

import java.io.File;
import java.io.StringReader;

import javax.xml.bind.Binder;
import javax.xml.bind.JAXBContext;

import junit.framework.TestCase;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.platform.xml.XMLTransformer;
import org.eclipse.persistence.testing.jaxb.JAXBXMLComparer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class BinderWithNullPolicyCompositeTestCases extends TestCase {

    private XMLParser parser;

    public BinderWithNullPolicyCompositeTestCases() {
        XMLPlatform platform = XMLPlatformFactory.getInstance().getXMLPlatform();
        parser = platform.newXMLParser();
    }
    
    public void testEmptyNode() throws Exception {
        String xml = "<employee><!-- Comment 1 --><name>Matt</name><!-- Comment 2 --><address><street>123 Fake Street</street><city>Kanata</city></address><phone>123-4567</phone><phone>234-5678</phone></employee>";
        String controlSource = "org/eclipse/persistence/testing/jaxb/binder/nullpolicy/emptynodecomposite.xml";
        Document controlDocument = parser.parse(new File(controlSource));
        
        JAXBContext ctx = JAXBContextFactory.createContext(new Class[]{EmployeeCompositeA.class}, null);
        
        Binder binder = ctx.createBinder();
        
        EmployeeCompositeA emp = (EmployeeCompositeA)binder.unmarshal(parser.parse(new StringReader(xml)));
        
        emp.address = null;
        
        emp.phone.add(1, null);
        
        binder.updateXML(emp);
        
        XMLTransformer transformer = XMLPlatformFactory.getInstance().getXMLPlatform().newXMLTransformer();
        transformer.transform(controlDocument, System.out);
        
        transformer.transform((Node)binder.getXMLNode(emp), System.out);
         
        JAXBXMLComparer comparer = new JAXBXMLComparer();
        assertTrue("Marshalled document does not match the control document.", comparer.isNodeEqual(controlDocument, ((Node)binder.getXMLNode(emp)).getOwnerDocument()));
    }
}
