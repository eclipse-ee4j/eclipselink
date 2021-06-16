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
