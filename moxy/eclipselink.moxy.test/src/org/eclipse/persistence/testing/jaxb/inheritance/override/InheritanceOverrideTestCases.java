/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Matt MacIvor - 2.5.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.inheritance.override;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;


public class InheritanceOverrideTestCases extends JAXBWithJSONTestCases {
    public InheritanceOverrideTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] { Root.class, Foo.class, Superclass.class, Subclass.class });
        setControlDocument("org/eclipse/persistence/testing/jaxb/inheritance/override.xml");
        setControlJSON("org/eclipse/persistence/testing/jaxb/inheritance/override.json");
    }

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
