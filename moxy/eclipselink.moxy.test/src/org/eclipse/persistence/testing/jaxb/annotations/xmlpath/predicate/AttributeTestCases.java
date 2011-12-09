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
 *     Blaise Doughan - 2.4 - initial implementation
 ******************************************************************************/
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