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
//     Blaise Doughan - 2.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlrootelement;

import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class InnerClassTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlrootelement/inner.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlrootelement/inner.json";

    public InnerClassTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = Inner.class;
        setClasses(classes);
    }

    @Override
    protected Object getControlObject() {
        return new Inner();
    }

    @XmlRootElement
    public static class Inner {

        @Override
        public boolean equals(Object o) {
            return o.getClass() == Inner.class;
        }

    }

}
