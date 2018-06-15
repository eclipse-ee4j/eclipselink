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
//     rbarkhouse - 2009-04-14 - 2.0 - Initial implementation

package org.eclipse.persistence.testing.oxm.mappings.compositeobject.keepaselement;

import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class CompositeObjectKeepUnknownAsElementTestCases extends XMLMappingTestCases {

    public CompositeObjectKeepUnknownAsElementTestCases(String name) throws Exception {
        super(name);
        setProject(new CompositeObjectKeepUnknownAsElementProject());
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/compositeobject/keepaselement/particlesA002.xml");
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
            Element elemElem1 = d.createElementNS(null, "elem1");
            myElem.setElem(elemElem);
            myElem.setElem1(elemElem1);

            Element docElem1 = d.createElementNS(null, "elem1");

            myDoc.setElem(myElem);
            myDoc.setElem1(docElem1);
        } catch(Exception ex) {
            fail(ex.getMessage());
        }

        return myDoc;
    }

}
