/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Praba Vijayaratnam - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmlelement;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlElementNillableRequiredTest extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmlelement/xmlelementnillrequired.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmlelement/xmlelementnillrequired.json";

    public XmlElementNillableRequiredTest(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = USPriceRequired.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        USPriceRequired p = new USPriceRequired();
        p.setPrice(123.99);

        return p;
    }


}
