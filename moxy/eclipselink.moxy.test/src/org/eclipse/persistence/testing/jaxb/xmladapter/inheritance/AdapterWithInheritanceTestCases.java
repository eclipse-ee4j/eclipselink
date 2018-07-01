/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
//  - rbarkhouse -01 March 2012 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmladapter.inheritance;

import javax.xml.bind.Marshaller;

import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class AdapterWithInheritanceTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/inheritance/root.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/inheritance/root.json";

    public AdapterWithInheritanceTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[] {Root.class};
        setClasses(classes);
    }

    protected Object getControlObject() {
        Root root = new Root();
        return root;
    }

    public Object getReadControlObject() {
        Root root = new Root();
        root.foo = new Foo();
        return root;
    }

}
