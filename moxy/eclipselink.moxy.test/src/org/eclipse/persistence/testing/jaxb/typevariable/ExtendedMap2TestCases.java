/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.5.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.typevariable;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class ExtendedMap2TestCases extends JAXBWithJSONTestCases {

    private static final String XML = "org/eclipse/persistence/testing/jaxb/typevariable/extendedMap.xml";
    private static final String JSON = "org/eclipse/persistence/testing/jaxb/typevariable/extendedMap.json";

    public ExtendedMap2TestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML);
        setControlJSON(JSON);
        setClasses(new Class[] {ExtendedMap2Root.class});
    }

    @Override
    protected ExtendedMap2Root getControlObject() {
        ExtendedMap2Root control = new ExtendedMap2Root();
        control.foo = new ExtendedMap2();
        control.foo.put(new Foo(1), new Bar());
        control.foo.put(new Foo(2), new Bar());
        return control;
    }

}
