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
//     Blaise Doughan - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.predicate;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class AttributeTestCases extends JAXBTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlpath/predicate/attribute.xml";

    public AttributeTestCases(String name) throws Exception {
        super(name);
        this.setClasses(new Class[] {AttributeRoot.class});
        this.setControlDocument(XML_RESOURCE);
    }

    @Override
    protected Object getControlObject() {
        AttributeRoot root = new AttributeRoot();
        root.setAddress("http://wwww.example.com/address/1");
        root.getPhoneNumber().add("http://wwww.example.com/phone-number/2");
        root.getPhoneNumber().add("http://wwww.example.com/phone-number/3");
        return root;
    }

}
