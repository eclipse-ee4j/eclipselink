/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Matt MacIvor - 2.4.2 - Initial Implementation
package org.eclipse.persistence.testing.jaxb.xmlelements;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class PredicateLinkTestCases extends JAXBWithJSONTestCases{

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelements/predicate_link.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelements/predicate_link.json";

    public PredicateLinkTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[]{CustomerPredicate.class});
    }

    @Override
    protected Object getControlObject() {
        CustomerPredicate cust = new CustomerPredicate();
        Link link = new Link();
        link.href = "link_location";
        cust.address = link;
        return cust;
    }
}
