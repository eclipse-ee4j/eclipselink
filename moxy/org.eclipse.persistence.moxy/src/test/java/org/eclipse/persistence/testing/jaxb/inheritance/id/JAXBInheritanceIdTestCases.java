/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - August 2013
package org.eclipse.persistence.testing.jaxb.inheritance.id;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class JAXBInheritanceIdTestCases extends JAXBWithJSONTestCases {
    public JAXBInheritanceIdTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class<?>[] {AImpl.class, BImpl.class, Base.class});
        setControlDocument("org/eclipse/persistence/testing/jaxb/inheritance/id/a.xml");
        setControlJSON("org/eclipse/persistence/testing/jaxb/inheritance/id/a.json");

    }

    @Override
    public Object getControlObject() {
        A a = new AImpl("a");
        B b = new BImpl("b");

        a.setB(b);
        b.setA(a);

        return a;
    }

}
