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
//     Matt MacIvor - October 2011 - 2.4
package org.eclipse.persistence.testing.jaxb.jaxbelement.subclass;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.jaxbelement.enumeration.Coin;

@XmlRegistry
public class ObjectFactoryEnum {
/*
     @XmlElementDecl(name="root2")
        public JAXBElement<Coin> createSubClass2(Coin theCoin) {
            return new JAXBElement(new QName("root"), Coin.class, theCoin);
        }
    */
    @XmlElementDecl(name="root")
    public SubClassEnum createSubClass(Coin theCoin) {
        return new SubClassEnum(new QName("root"), Coin.class, theCoin);
    }
}
