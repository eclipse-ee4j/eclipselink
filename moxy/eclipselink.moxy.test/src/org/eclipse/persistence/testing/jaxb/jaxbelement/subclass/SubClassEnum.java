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
//     Denise Smith - January 2012 - 2.3
package org.eclipse.persistence.testing.jaxb.jaxbelement.subclass;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.jaxbelement.enumeration.Coin;

@XmlRootElement(name = "root")
public class SubClassEnum extends JAXBElement<Coin> {

    public SubClassEnum(QName name, Class<Coin> declaredType, Coin value) {
        super(name, declaredType, value);
    }
    /*
    public SubClassEnum(QName name, Class<Coin> declaredType, Coin value) {
        super(name, declaredType, value);
    }
    */
    public SubClassEnum() {
        this(new QName("root"), Coin.class, Coin.QUARTER);
    }

}
