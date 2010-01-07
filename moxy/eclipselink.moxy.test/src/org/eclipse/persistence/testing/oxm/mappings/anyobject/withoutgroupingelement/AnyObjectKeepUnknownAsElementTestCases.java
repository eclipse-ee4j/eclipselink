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
package org.eclipse.persistence.testing.oxm.mappings.anyobject.withoutgroupingelement;

import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class AnyObjectKeepUnknownAsElementTestCases extends XMLMappingTestCases {

    public AnyObjectKeepUnknownAsElementTestCases(String name) throws Exception {
        super(name);
        setProject(new AnyObjectKeepUnkownAsElementProject());
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/anyobject/withoutgroupingelement/keep_unknown_as_element.xml");
    }
    
    public Object getControlObject() {
        RootKeepAsElement root = new RootKeepAsElement();
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setIgnoringElementContentWhitespace(true);
        
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element rootElem = doc.createElementNS(null, "t1");
            root.setT1(rootElem);
        } catch(Exception ex) {
            fail(ex.getMessage());
        }
        
        return root;
    }
    
}
