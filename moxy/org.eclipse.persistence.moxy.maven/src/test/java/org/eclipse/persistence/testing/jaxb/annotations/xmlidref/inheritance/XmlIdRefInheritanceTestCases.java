/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Radek Felcman - 2.7.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlidref.inheritance;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

import java.util.ArrayList;
import java.util.List;

public class XmlIdRefInheritanceTestCases extends JAXBWithJSONTestCases {

    private static final String CONTROL_JSON = "org/eclipse/persistence/testing/jaxb/annotations/xmlidref/inheritance/control.json";
    private static final String CONTROL_XML = "org/eclipse/persistence/testing/jaxb/annotations/xmlidref/inheritance/control.xml";

    public XmlIdRefInheritanceTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {RootNode.class});
        setControlJSON(CONTROL_JSON);
        setControlDocument(CONTROL_XML);
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_MARSHAL_EMPTY_COLLECTIONS, Boolean.TRUE);
    }

    @Override
    protected RootNode getControlObject() {

        ComplexNode complexNode1 = new ComplexNode();
        complexNode1.setId("10");
        complexNode1.setName("complexNode10");

        LeafNode leafNode1 = new LeafNode();
        leafNode1.setId("20");
        leafNode1.setName("leafNode10");

        List<Node> nodes = new ArrayList<>();
        nodes.add(complexNode1);
        nodes.add(leafNode1);

        complexNode1.setRefNodes(nodes);

        leafNode1.setRefNodes(nodes);
        leafNode1.setTargetNode(complexNode1);

        RootNode rootNode = new RootNode();
        rootNode.setId("1");
        rootNode.setName("rootNode1");
        rootNode.setRefNodes(nodes);
        rootNode.setOwnedNodes(nodes);

        return rootNode;
    }

    public void testSchemaGen() throws Exception {
        MySchemaOutputResolver outputResolver = new MySchemaOutputResolver();
        getJAXBContext().generateSchema(outputResolver);

        assertEquals("A Schema was generated but should not have been", 1, outputResolver.getSchemaFiles().size());
    }


}
