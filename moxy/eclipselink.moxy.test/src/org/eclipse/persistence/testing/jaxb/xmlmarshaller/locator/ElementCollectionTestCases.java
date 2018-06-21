/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlmarshaller.locator;

public class ElementCollectionTestCases extends LocatorTestCase {

    public ElementCollectionTestCases(String name) {
        super(name);
    }

    @Override
    public Class[] getClasses() {
        Class[] classes = {ElementCollectionRoot.class, Child.class};
        return classes;
    }

    @Override
    public ElementCollectionRoot setupRootObject() {
        ElementCollectionRoot control = new ElementCollectionRoot();
        control.setName("123456789");
        control.getChild().add(child);
        return control;
    }

}
