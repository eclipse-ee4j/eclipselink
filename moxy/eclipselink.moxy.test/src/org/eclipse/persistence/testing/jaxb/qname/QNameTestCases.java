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