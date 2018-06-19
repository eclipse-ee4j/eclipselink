/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     bdoughan - March 11/2010 - 2.0.2 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.compositecollection.norefclass;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class DefaultNSTestCases  extends XMLMappingTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositeobject/norefclass/DefaultNS.xml";

    public DefaultNSTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setProject(new CustomerProject());
    }

    @Override
    protected Object getControlObject() {
        Customer customer = new Customer();
        List<Address> address = new ArrayList<Address>();
        address.add(new Address());
        customer.setAddress(address);
        return customer;
    }

}
