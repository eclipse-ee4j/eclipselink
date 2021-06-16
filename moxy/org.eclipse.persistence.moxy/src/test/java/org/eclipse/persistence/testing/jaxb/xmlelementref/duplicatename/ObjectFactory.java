/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     mmacivor - 2010-03-09 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlelementref.duplicatename;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
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

