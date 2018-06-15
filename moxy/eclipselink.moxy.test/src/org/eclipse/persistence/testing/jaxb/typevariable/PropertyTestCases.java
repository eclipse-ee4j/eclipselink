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

public class PropertyTestCases extends JAXBWithJSONTestCases {

    private static final String XML = "org/eclipse/persistence/testing/jaxb/typevariable/property.xml";
    private static final String JSON = "org/eclipse/persistence/testing/jaxb/typevariable/property.json";

    public PropertyTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML);
        setControlJSON(JSON);
        setClasses(new Class[] {PropertyRoot.class});
    }

    @Override
    protected PropertyRoot getControlObject() {
        PropertyRoot<Foo, Bar> control = new PropertyRoot<Foo, Bar>();
        control.foo = new Foo();
        control.bar.add(new Bar());
        control.bar.add(new Bar());
        return control;
    }

}
