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
//     Denise Smith - February 2012
package org.eclipse.persistence.testing.jaxb.xmlelementref.collections;


import jakarta.activation.DataHandler;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlMimeType;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


@XmlRegistry
public class ObjectFactory {

    private final static QName _WrapperReturn_QNAME = new QName("", "return");
    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: mtom.service.types
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Wrapper }
     *
     */
    public Wrapper createWrapper() {
        return new Wrapper();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataHandler }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "return", scope = Wrapper.class)
    @XmlMimeType("application/octet-stream")
    public JAXBElement<DataHandler> createWrapperReturn(DataHandler value) {
        return new JAXBElement<DataHandler>(_WrapperReturn_QNAME, DataHandler.class, Wrapper.class, value);
    }

}
