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
//     Denise Smith  January 26, 2010 - 2.0.1
package org.eclipse.persistence.testing.jaxb.xmlelementref.missingref;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class MissingRefObjectFactory {

    private final static QName theQName = new QName("", "arg0");

    public MissingRefObjectFactory() {
    }

    @XmlElementDecl(namespace = "", name = "arg0", scope = Person.class)
    public JAXBElement<byte[]> createEchoByteArrayArg0(byte[] value) {
        return new JAXBElement<byte[]>(theQName, byte[].class, Person.class, ((byte[]) value));
    }
}
