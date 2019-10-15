/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.predicate.ns;

import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class QNameTestCases extends JAXBTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlpath/predicate/ns/qname.xml";
    private static final String CONTROL_LOCAL_NAME_1 = "LOCAL_ONE";
    private static final String CONTROL_LOCAL_NAME_2 = "LOCAL_TWO";
    private static final String CONTROL_LOCAL_NAME_3 = "LOCAL_THREE";

    public QNameTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {QNameRoot.class});
        setControlDocument(XML_RESOURCE);
    }

    @Override
    protected Object getControlObject() {
        QNameRoot root = new QNameRoot();
        root.setName(new QName("", CONTROL_LOCAL_NAME_1));
        root.getNameList().add(new QName(CONTROL_LOCAL_NAME_2));
        root.getNameList().add(new QName(CONTROL_LOCAL_NAME_3));
        return root;
    }

}
