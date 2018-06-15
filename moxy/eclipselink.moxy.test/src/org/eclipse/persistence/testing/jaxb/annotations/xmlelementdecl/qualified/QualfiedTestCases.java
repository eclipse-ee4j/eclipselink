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
//     Blaise Doughan - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlelementdecl.qualified;

import javax.xml.bind.JAXBElement;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class QualfiedTestCases extends JAXBWithJSONTestCases {

    private static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlelementdecl/qualified.xml";
    private static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlelementdecl/qualified.json";

    public QualfiedTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[] {ComplexType.class, ObjectFactory.class});
    }

    @Override
    protected JAXBElement<ComplexType> getControlObject() {
        ObjectFactory objectFactory = new ObjectFactory();
        ComplexType complexType = objectFactory.createComplexType();
        JAXBElement<ComplexType> root = objectFactory.createRoot(complexType);
        complexType.setGlobal(true);
        complexType.setLocal(objectFactory.createComplexTypeTestLocal("1.1"));
        return root;
    }

}
