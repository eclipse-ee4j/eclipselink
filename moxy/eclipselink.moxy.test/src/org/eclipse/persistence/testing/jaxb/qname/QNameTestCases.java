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
package org.eclipse.persistence.testing.jaxb.qname;

import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class QNameTestCases extends JAXBWithJSONTestCases {

    public QNameTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] { Root.class });
        setControlDocument("org/eclipse/persistence/testing/jaxb/qname/qname.xml");
        setControlJSON("org/eclipse/persistence/testing/jaxb/qname/qname.json");
    }

    @Override
    protected Object getControlObject() {
        Root root = new Root();
        root.setAttribute(new QName("urn:a", "a"));
        root.setElement(new QName("urn:b", "b"));
        root.getList().add(new QName("urn:c", "c"));
        root.getList().add(new QName("urn:d", "d"));
        return root;
    }

}
