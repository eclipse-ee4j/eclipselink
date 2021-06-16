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
//     Denise Smith - January 2012
package org.eclipse.persistence.testing.jaxb.annotations.xmlelementdecl.emptystringns;

import jakarta.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class EmptyStringNSTestCases extends JAXBWithJSONTestCases {

    private static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlelementdecl/emptystringns.xml";
    private static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlelementdecl/emptystringns.json";

    public EmptyStringNSTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[] {TestObject.class, ObjectFactory.class});
    }

    @Override
    protected Object getControlObject() {
        ObjectFactory objectFactory = new ObjectFactory();
        TestObject testObject = objectFactory.createTestObject();
        JAXBElement elem = new JAXBElement<TestObject>(new QName("testObject"), TestObject.class, testObject);

        return elem;
    }


}
