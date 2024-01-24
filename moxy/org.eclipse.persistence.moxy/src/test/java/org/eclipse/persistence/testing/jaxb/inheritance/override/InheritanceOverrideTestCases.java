/*
 * Copyright (c) 2012, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Matt MacIvor - 2.5.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.inheritance.override;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;


public class InheritanceOverrideTestCases extends JAXBWithJSONTestCases {
    public InheritanceOverrideTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class<?>[] { Root.class, Foo.class, Superclass.class, Subclass.class });
        setControlDocument("org/eclipse/persistence/testing/jaxb/inheritance/override.xml");
        setControlJSON("org/eclipse/persistence/testing/jaxb/inheritance/override.json");
    }

    @Override
    public Object getControlObject() {
        Root r = new Root();
        Subclass s = new Subclass();
        r.setSubclass(s);
        Foo f = new Foo();
        f.setName("abc");
        s.getCollection().add(f);

        return r;
    }
}
