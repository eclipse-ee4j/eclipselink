/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.2 - initial implementation
 ******************************************************************************/
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