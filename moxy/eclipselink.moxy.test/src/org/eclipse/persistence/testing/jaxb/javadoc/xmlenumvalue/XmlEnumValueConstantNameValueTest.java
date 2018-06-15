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
package org.eclipse.persistence.testing.jaxb.javadoc.xmlenumvalue;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlEnumValueConstantNameValueTest extends JAXBWithJSONTestCases{

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmlenumvalue/xmlenumvalueconstantname.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmlenumvalue/xmlenumvalueconstantname.json";

    public XmlEnumValueConstantNameValueTest(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[3];
        classes[0] = Coin.class;
        classes[1] = Card.class;
        classes[2] = XmlEnumValueConstantNameValue.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        XmlEnumValueConstantNameValue example = new XmlEnumValueConstantNameValue();
        example.setCoin(Coin.QUARTER);
        example.setCard(Card.HEARTS);
        return example;
    }


}
