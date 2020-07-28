/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.namespaceuri.twopackages;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.namespaceuri.twopackages.a.A;
import org.eclipse.persistence.testing.jaxb.namespaceuri.twopackages.b.B;

public class ABTestCases extends JAXBWithJSONTestCases{
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/namespaceuri/twopackages/ab.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/namespaceuri/twopackages/ab.json";

    public ABTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {A.class, B.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    @Override
    protected A getControlObject() {
        A a = new A();
        B b = new B();
        b.setC("C");
        a.setB(b);
        return a;
    }

}
