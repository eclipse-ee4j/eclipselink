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
 *     Blaise Doughan - 2.3 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.enumtype;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class EnumTestCases extends JAXBTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlpath/enumtype.xml";

    public EnumTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {Customer.class});
        setControlDocument(XML_RESOURCE);
    }

    @Override
    protected Customer getControlObject() {
        Customer customer = new Customer();
        customer.setLevel(StatusEnum.GOLD);
        return customer;
    }

}