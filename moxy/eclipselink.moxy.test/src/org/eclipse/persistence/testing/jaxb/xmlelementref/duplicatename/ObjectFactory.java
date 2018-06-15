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
//     mmacivor - 2010-03-09 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlelementref.duplicatename;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {

    public BeanA createBeanA() {
        return new BeanA();
    }

    public BeanB createBeanB() {
        return new BeanB();
    }

    @XmlElementDecl(name="value", scope=BeanA.class)
    public JAXBElement<byte[]> createBeanAValue() {
        return new JAXBElement<byte[]>(new QName("value"), byte[].class, new byte[]{1, 2, 3 ,4});
    }

    @XmlElementDecl(name="value", scope=BeanB.class)
    public JAXBElement<Integer> createBeanBValue() {
        return new JAXBElement<Integer>(new QName("value"), Integer.class, new Integer(12));
    }

    @XmlElementDecl(name="value")
    public JAXBElement<String> createGlobalElem() {
        return new JAXBElement<String>(new QName("value"), String.class, "default-value");
    }
}

