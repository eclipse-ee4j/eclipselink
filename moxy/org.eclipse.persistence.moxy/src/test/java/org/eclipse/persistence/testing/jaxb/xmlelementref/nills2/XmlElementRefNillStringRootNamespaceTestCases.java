/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.xmlelementref.nills2;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

import jakarta.xml.bind.JAXBElement;
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
