/*******************************************************************************
 * Copyright (c) 2013, 2015  Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.5.1 - initial implementation
 ******************************************************************************/
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
