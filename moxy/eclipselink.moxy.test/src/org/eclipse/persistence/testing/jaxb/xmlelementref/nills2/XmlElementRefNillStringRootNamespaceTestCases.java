/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.xmlelementref.nills2;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class XmlElementRefNillStringRootNamespaceTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/stringNillRootNamespace.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/stringNillRootNamespace.json";

    public XmlElementRefNillStringRootNamespaceTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class<?>[]{ObjectFactory.class, Bar.class, OptFoo.class});
    }

    @Override
    protected Object getControlObject() {

        JAXBElement<Bar> bar = new JAXBElement<>(new QName("NS", "bar"), Bar.class, OptFoo.class, null);
        bar.setValue(new Bar());
        bar.setNil(true);

        JAXBElement<OptFoo> foo = new JAXBElement<>(new QName("NS", "optFoo-Root"), OptFoo.class, null, new OptFoo());
        foo.getValue().setBar(bar);

        return foo;
    }

}
