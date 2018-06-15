/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlelementref.enums;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class Registry {

    @XmlElementDecl(name="string")
    public JAXBElement<String> createString(String value) {
        return new JAXBElement<String>(new QName("string"),String.class, value);
    }

    @XmlElementDecl(name="enum")
    public JAXBElement<MyEnum> createEnum(MyEnum value) {
        return new JAXBElement<MyEnum>(new QName("enum"),MyEnum.class, value);
    }

}
