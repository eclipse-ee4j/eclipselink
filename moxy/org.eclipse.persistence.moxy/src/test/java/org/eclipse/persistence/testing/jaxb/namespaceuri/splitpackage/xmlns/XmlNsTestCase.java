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
//     Matt MacIvor - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.namespaceuri.splitpackage.xmlns;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.namespaceuri.splitpackage.xmlns.a.Customer;
import org.eclipse.persistence.testing.jaxb.namespaceuri.splitpackage.xmlns.b.Address;

public class XmlNsTestCase extends JAXBWithJSONTestCases {

    public XmlNsTestCase(String name) throws Exception {
        super(name);
        setClasses(new Class[] { Customer.class, Address.class });
        setControlDocument("org/eclipse/persistence/testing/jaxb/namespaceuri/splitpackage/xmlns/cust.xml");
        setControlJSON("org/eclipse/persistence/testing/jaxb/namespaceuri/splitpackage/xmlns/cust.json");
    }

    @Override
    protected Object getControlObject() {
        Customer cust = new Customer();
        cust.setAddress(new Address());

        return cust;
    }



}
