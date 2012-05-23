/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - 17 November 2011 - 2.3.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlelementref.ns;

import java.util.Arrays;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "request" })
@XmlRootElement(name = "EchoByteArray")
public class EchoByteArray {

    @XmlElementRef(name = "request", namespace = "http://missing-uri.org/", type = JAXBElement.class)
    protected JAXBElement<byte[]> request;

    public JAXBElement<byte[]> getRequest() {
        return request;
    }

    public void setRequest(JAXBElement<byte[]> value) {
        this.request = value;
    }

    public boolean equals(Object obj) {
        if (obj instanceof EchoByteArray) {
            EchoByteArray eba = (EchoByteArray) obj;
            try {
                return Arrays.equals(eba.request.getValue(), this.request.getValue());
            } catch (NullPointerException e) {
                return false;
            }
        }
        return false;
    }

    public String toString() {
        return getClass().getSimpleName() + (request == null ? " [null]" : request.getValue());
    }

}