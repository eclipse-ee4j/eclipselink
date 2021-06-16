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
//    Denise Smith - June 2013
package org.eclipse.persistence.testing.jaxb.xmlelementref;

import java.util.List;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class FooObjectFactory {

    private final static QName _Things_QNAME = new QName("", "things");

    public FooObjectFactory() {
    }

    @XmlElementDecl(namespace = "", name = "things")
    public JAXBElement<List<byte[]>> createThings(List<byte[]> value) {
       return new JAXBElement<List<byte[]>>(_Things_QNAME, ((Class) List.class),((List<byte[]> ) value));
    }
}
