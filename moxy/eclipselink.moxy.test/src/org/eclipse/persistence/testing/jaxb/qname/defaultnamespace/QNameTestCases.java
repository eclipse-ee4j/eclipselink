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
//     Matt MacIvor - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.qname.defaultnamespace;

import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class QNameTestCases extends JAXBWithJSONTestCases {

    public QNameTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] { Root.class });
        setControlDocument("org/eclipse/persistence/testing/jaxb/qname/defaultnamespace/qname.xml");
        setControlJSON("org/eclipse/persistence/testing/jaxb/qname/defaultnamespace/qname.json");
    }

    @Override
    protected Object getControlObject() {
        Root root = new Root();

        root.qname =  new QName("", "localPart1");

        root.listOfNames = new ArrayList<QName>();
        root.listOfNames.add(new QName("myns1", "localPart2"));
        root.listOfNames.add(new QName("", "localPart3"));
        root.listOfNames.add(new QName("myns", "localPart4"));

        return root;
    }

}
