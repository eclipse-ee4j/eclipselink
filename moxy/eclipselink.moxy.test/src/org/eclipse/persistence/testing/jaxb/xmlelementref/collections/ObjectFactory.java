/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - February 2012
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlelementref.collections;


import javax.activation.DataHandler;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlRegistry;
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