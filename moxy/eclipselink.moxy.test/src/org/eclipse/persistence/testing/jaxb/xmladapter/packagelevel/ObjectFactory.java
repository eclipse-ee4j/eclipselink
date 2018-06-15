/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// Denise Smith - September 10 /2009
package org.eclipse.persistence.testing.jaxb.xmladapter.packagelevel;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.xmlmodel.XmlEnumValue;

@XmlRegistry
public class ObjectFactory {

    public ClassB createClassB() {
        return new ClassB();
    }

    public ClassA createClassA() {
        return new ClassA();
    }

    public TestObject createTestObject() {
        return new TestObject();
    }
}
