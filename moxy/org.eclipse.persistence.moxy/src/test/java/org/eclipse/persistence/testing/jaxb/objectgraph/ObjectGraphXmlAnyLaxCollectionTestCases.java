/*
 * Copyright (c) 2014, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Martin Vojtek - 2.6.0 - initial implementation
package org.eclipse.persistence.testing.jaxb.objectgraph;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.JAXBHelper;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.ObjectGraph;
import org.eclipse.persistence.jaxb.Subgraph;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

/**
 * Tests marshalling of collection of JAXBElement objects annotated with @XmlAnyElement(lax=true) filtered by ObjectGraph.
 *
 * @author Martin Vojtek
 *
 */
public class ObjectGraphXmlAnyLaxCollectionTestCases extends JAXBWithJSONTestCases {

    private final static String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/objectgraph/any_lax_collection_write.xml";
    private final static String JSON_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/objectgraph/any_lax_collection_write.json";

    public ObjectGraphXmlAnyLaxCollectionTestCases(String name) throws Exception {
        super(name);
        this.setClasses(new Class[]{RootElement.class, LaxElements.class});
        setWriteControlDocument(XML_WRITE_RESOURCE);
        setWriteControlJSON(JSON_WRITE_RESOURCE);

        ObjectGraph laxFooObjectGraph = JAXBHelper.getJAXBContext(jaxbContext)
                .createObjectGraph(RootElement.class);

        Subgraph itemsSubgraph = laxFooObjectGraph.addSubgraph("items");
        itemsSubgraph.addAttributeNodes("element1");

        Subgraph element3Subgraph = itemsSubgraph.addSubgraph("element3");
        element3Subgraph.addAttributeNodes("element4");
        itemsSubgraph.addAttributeNodes("element8");

        jaxbMarshaller.setProperty(MarshallerProperties.OBJECT_GRAPH, laxFooObjectGraph);
    }

    @Override
    protected Object getControlObject() {

        RootElement root = new RootElement();

        List<JAXBElement> items = new ArrayList<JAXBElement>(3);
        items.add(new JAXBElement(new QName("element1"), String.class, "element1Value"));
        items.add(new JAXBElement(new QName("element2"), Integer.class, 1));

        LaxElements element3 = new LaxElements();
        element3.setElement4("e1");
        element3.setElement5("e2");
        element3.setElement6("e3");
        items.add(new JAXBElement(new QName("element3"), LaxElements.class, element3));
        items.add(new JAXBElement(new QName("element7"), String.class, "element7Value"));
        items.add(new JAXBElement(new QName("element8"), Integer.class, 8));

        root.items = items;

        return root;
    }

    @Override
    public boolean isUnmarshalTest() {
        return false;
    }

}
