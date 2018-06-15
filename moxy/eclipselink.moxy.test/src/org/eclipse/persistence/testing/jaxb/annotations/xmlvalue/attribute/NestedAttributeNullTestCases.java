/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Matt MacIvor - 2.5.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlvalue.attribute;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class NestedAttributeNullTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlvalue/nestedAttributeNull.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlvalue/nestedAttributeNull.json";

    public NestedAttributeNullTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {Top.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    @Override
    public Top getControlObject() {
        Bottom bottom = new Bottom();
        bottom.value = null;

        Middle2 middle2 = new Middle2();
        middle2.bottom = bottom;
        bottom.middle2 = middle2;

        Middle1 middle1 = new Middle1();
        middle1.middle2 = middle2;
        middle2.middle1 = middle1;

        Top top = new Top();
        top.middle1 = middle1;
        middle1.top = top;
        return top;
    }

    @Override
    public Top getReadControlObject() {

        Top top = new Top();
        Middle1 m = new Middle1();
        top.middle1 = m;
        m.top = top;

        return top;

    }

}
