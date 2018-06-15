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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

@XmlRootElement(name = "root")
public class SubClass extends JAXBElement<byte[]> {

    public SubClass(QName name, Class<byte[]> declaredType, byte[] value) {
        super(name, declaredType, value);
    }

    public SubClass() {
        this(new QName("root"), byte[].class, new byte[] {1, 2, 3, 4});
    }

}
