/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
// dmccann - 2.2 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.anyobject.withoutgroupingelement;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class AnyObjectKeepAllAsElementTestCases extends XMLWithJSONMappingTestCases {
    private static final String TEXT = "jim";
    private static final String CHILD = "child";

    public AnyObjectKeepAllAsElementTestCases(String name) throws Exception {
        super(name);
        setProject(new AnyObjectKeepAllAsElementProject());
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/anyobject/withoutgroupingelement/keep_all_as_element.xml");
        setControlJSON("org/eclipse/persistence/testing/oxm/mappings/anyobject/withoutgroupingelement/keep_all_as_element.json");
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
