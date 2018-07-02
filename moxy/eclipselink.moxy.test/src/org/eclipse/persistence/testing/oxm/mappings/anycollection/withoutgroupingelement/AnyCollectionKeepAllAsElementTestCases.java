/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.anycollection.withoutgroupingelement;

/**
 *  @version $Header: AnyCollectionKeepAllAsElementTestCases.java 30-jul-2007.15:34:44 dmccann Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */

import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class AnyCollectionKeepAllAsElementTestCases extends XMLMappingTestCases {
    private static String XML_CHILD_ELEMENTS = "org/eclipse/persistence/testing/oxm/mappings/anycollection/withoutgroupingelement/child_elements.xml";
    public AnyCollectionKeepAllAsElementTestCases(String name) throws Exception {
        super(name);
        setProject(new AnyCollectionWithoutGroupingElementKeepAllAsElementProject());
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/anycollection/withoutgroupingelement/keep_as_element.xml");
    }

    public Object getControlObject() {
        Root root = new Root();
        Vector any = new Vector();
        root.setAny(any);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setIgnoringElementContentWhitespace(true);
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(getClass().getClassLoader().getResourceAsStream(XML_CHILD_ELEMENTS));
            Element rootElem = doc.getDocumentElement();
            NodeList children = rootElem.getChildNodes();
            for(int i = 0; i < children.getLength(); i++) {
                if(children.item(i).getNodeType() == Element.ELEMENT_NODE) {
                    any.add(children.item(i));
                }
            }
        } catch(Exception ex) {}

        return root;
    }
}


