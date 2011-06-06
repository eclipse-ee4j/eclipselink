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
 *     rbarkhouse - 2009-04-14 - 2.0 - Initial implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.oxm.mappings.compositecollection.keepaselement;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class CompositeCollectionKeepUnknownAsElementTestCases extends XMLMappingTestCases {

    public CompositeCollectionKeepUnknownAsElementTestCases(String name) throws Exception {
        super(name);
        setProject(new CompositeCollectionKeepUnknownAsElementProject());
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/compositecollection/keepaselement/keepaselement.xml");
    }
    
    public Object getControlObject() {
        Doc myDoc = new Doc();
        Elem myElem = new Elem();
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setIgnoringElementContentWhitespace(true);
        
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document d = builder.newDocument();
            
            Element elemElem = d.createElementNS(null, "elem");
            Element elemElem2 = d.createElementNS(null, "elem");
            List myElems = new ArrayList();
            myElems.add(elemElem);
            myElems.add(elemElem2);
            myElem.setElem(myElems);
            
            List myElem1s = new ArrayList();
            Element elemElem1 = d.createElementNS(null, "elem1");
            Element elemElem12 = d.createElementNS(null, "elem1");
            Element elemElem13 = d.createElementNS(null, "elem1");
            myElem1s.add(elemElem1);
            myElem1s.add(elemElem12);
            myElem1s.add(elemElem13);
            myElem.setElem1(myElem1s);
                        
            List myDocElems = new ArrayList();
            myDocElems.add(myElem);
            myDoc.setElem(myDocElems);
            
            List docElems = new ArrayList();
            Element docElem1 = d.createElementNS(null, "elem1");
            Element docElem2 = d.createElementNS(null, "elem1");
            docElems.add(docElem1);
            docElems.add(docElem2);
            docElems.add(Boolean.FALSE);
            myDoc.setElem1(docElems);
            
        } catch(Exception ex) {
            fail(ex.getMessage());
        }
        
        return myDoc;
    }
        
}