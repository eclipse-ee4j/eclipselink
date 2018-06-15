/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Martin Vojtek - 2.6.0 - initial implementation
package org.eclipse.persistence.testing.jaxb.objectgraph;

import org.eclipse.persistence.jaxb.JAXBHelper;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.ObjectGraph;
import org.eclipse.persistence.jaxb.Subgraph;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

/**
 * Tests marshalling of Object annotated with @XmlAnyElement(lax=true) filtered by ObjectGraph.
 *
 * @author Martin Vojtek
 *
 */
public class ObjectGraphXmlAnyLaxTestCases extends JAXBWithJSONTestCases {

    private final static String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/objectgraph/any_lax_write.xml";
    private final static String JSON_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/objectgraph/any_lax_write.json";

    public ObjectGraphXmlAnyLaxTestCases(String name) throws Exception {
        super(name);
        this.setClasses(new Class[]{Elements.class, LaxFoo.class});
        setWriteControlDocument(XML_WRITE_RESOURCE);
        setWriteControlJSON(JSON_WRITE_RESOURCE);

        ObjectGraph laxFooObjectGraph = JAXBHelper.getJAXBContext(jaxbContext)
                .createObjectGraph(LaxFoo.class);

        Subgraph itemsSubgraph = laxFooObjectGraph.addSubgraph("elements");
        itemsSubgraph.addAttributeNodes("element1");
        itemsSubgraph.addAttributeNodes("element3");

        jaxbMarshaller.setProperty(MarshallerProperties.OBJECT_GRAPH, laxFooObjectGraph);
    }

    @Override
    protected Object getControlObject() {

        LaxFoo laxFoo = new LaxFoo();

        Elements elements = new Elements();
        elements.setElement1("element1Value");
        elements.setElement2("element2Value");
        elements.setElement3("element3Value");

        laxFoo.setElements(elements);

        return laxFoo;
    }

    @Override
    public boolean isUnmarshalTest() {
        return false;
    }

}
