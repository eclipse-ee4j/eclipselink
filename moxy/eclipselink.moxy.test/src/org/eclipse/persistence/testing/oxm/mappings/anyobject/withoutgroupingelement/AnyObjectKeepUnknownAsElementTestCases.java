/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.anyobject.withoutgroupingelement;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class AnyObjectKeepUnknownAsElementTestCases extends XMLWithJSONMappingTestCases {

    public AnyObjectKeepUnknownAsElementTestCases(String name) throws Exception {
        super(name);
        setProject(new AnyObjectKeepUnkownAsElementProject());
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/anyobject/withoutgroupingelement/keep_unknown_as_element.xml");
        setControlJSON("org/eclipse/persistence/testing/oxm/mappings/anyobject/withoutgroupingelement/keep_unknown_as_element.json");
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

