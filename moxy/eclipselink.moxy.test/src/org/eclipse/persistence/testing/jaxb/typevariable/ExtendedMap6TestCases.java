/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.5.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.typevariable;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class ExtendedMap6TestCases extends JAXBWithJSONTestCases {

    private static final String XML = "org/eclipse/persistence/testing/jaxb/typevariable/extendedMap.xml";
    private static final String JSON = "org/eclipse/persistence/testing/jaxb/typevariable/extendedMap.json";

    public ExtendedMap6TestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML);
        setControlJSON(JSON);
        setClasses(new Class[] {ExtendedMap6Root.class});
    }

    @Override
    protected ExtendedMap6Root getControlObject() {
        ExtendedMap6Root control = new ExtendedMap6Root();
        control.foo = new ExtendedMap6<Foo, Bar>();
        control.foo.put(new Foo(1), new Bar());
        control.foo.put(new Foo(2), new Bar());
        return control;
    }

}
