/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.namespaceuri.twopackages;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.namespaceuri.twopackages.a.A;
import org.eclipse.persistence.testing.jaxb.namespaceuri.twopackages.b.B;

public class BATestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/namespaceuri/twopackages/ba.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/namespaceuri/twopackages/ba.json";

    public BATestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {B.class, A.class});
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
