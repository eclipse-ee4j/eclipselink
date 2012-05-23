/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3.3 - initial implementation
 ******************************************************************************/
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