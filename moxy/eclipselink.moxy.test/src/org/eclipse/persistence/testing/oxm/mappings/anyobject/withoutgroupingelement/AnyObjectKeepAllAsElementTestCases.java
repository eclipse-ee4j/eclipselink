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
 * dmccann - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.anyobject.withoutgroupingelement;

import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class AnyObjectKeepAllAsElementTestCases extends XMLMappingTestCases {
    private static final String TEXT = "jim";
    private static final String CHILD = "child";
    
    public AnyObjectKeepAllAsElementTestCases(String name) throws Exception {
        super(name);
        setProject(new AnyObjectKeepAllAsElementProject());
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/anyobject/withoutgroupingelement/keep_all_as_element.xml");
    }
    
    public Object getControlObject() {
        RootKeepAsElement root = new RootKeepAsElement();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element rootElem = doc.createElementNS(null, CHILD);
            rootElem.setTextContent(TEXT);
            // because the descriptor will be known during unmarshal/marshal, the element will not
            // be wrapped in an XMLRoot - just set the Element on the RootKeepAsElement object
            root.setT1(rootElem);
        } catch(Exception ex) {
            fail(ex.getMessage());
        }
        return root;
    }

    public Object getWriteControlObject() {
        Child child = new Child();
        child.setContent(TEXT);
        XMLRoot xmlRoot = new XMLRoot();
        xmlRoot.setLocalName(CHILD);
        xmlRoot.setObject(child);
        RootKeepAsElement root = new RootKeepAsElement();
        root.setT1(xmlRoot);
        return root;
    }
}
