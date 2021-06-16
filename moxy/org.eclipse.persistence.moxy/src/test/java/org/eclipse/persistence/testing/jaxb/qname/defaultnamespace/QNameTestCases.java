/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
