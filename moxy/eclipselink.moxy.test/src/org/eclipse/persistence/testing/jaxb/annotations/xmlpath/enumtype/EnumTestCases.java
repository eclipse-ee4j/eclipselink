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
//     Blaise Doughan - 2.3 - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.enumtype;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class EnumTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlpath/enumtype.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlpath/enumtype.json";

    public EnumTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {Customer.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    @Override
    protected Customer getControlObject() {
        Customer customer = new Customer();
        customer.setLevel(StatusEnum.GOLD);
        return customer;
    }

}
