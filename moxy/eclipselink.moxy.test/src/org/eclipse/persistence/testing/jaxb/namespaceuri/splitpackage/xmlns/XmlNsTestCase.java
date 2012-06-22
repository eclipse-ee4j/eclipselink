/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Matt MacIvor - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.namespaceuri.splitpackage.xmlns;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.namespaceuri.splitpackage.xmlns.a.Customer;
import org.eclipse.persistence.testing.jaxb.namespaceuri.splitpackage.xmlns.b.Address;
import org.eclipse.persistence.testing.jaxb.qname.Root;

public class XmlNsTestCase extends JAXBTestCases {

    public XmlNsTestCase(String name) throws Exception {
        super(name);
        setClasses(new Class[] { Customer.class, Address.class });
        setControlDocument("org/eclipse/persistence/testing/jaxb/namespaceuri/splitpackage/xmlns/cust.xml");        
    }

    @Override
    protected Object getControlObject() {
        Customer cust = new Customer();
        cust.setAddress(new Address());
        
        return cust;
    }
    
    

}
