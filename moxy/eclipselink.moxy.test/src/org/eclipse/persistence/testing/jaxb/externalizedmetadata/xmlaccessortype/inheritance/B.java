/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//  - mmacivor - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype.inheritance;

import javax.xml.bind.annotation.XmlElement;

public class B extends A {

    String x;

    public B() {
        x = "Hello World";
    }

    @Override
    public String getX() {
        return x;
    }

    @XmlElement
    public TestClass getTestSub() {
        return new TestClass();
    }

    @XmlElement
    public TestSuperclass getTestSuper() {
        return new TestSuperclass();
    }

    public String getCalculatedValue() {
        return "Calculated Value";
    }

    public boolean equals(Object b) {
        return x.equals(((B) b).getX());
    }

}
