/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//  - rbarkhouse - 17 November 2011 - 2.3.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmlelementref.ns;

import java.util.Arrays;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

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
        if (obj instanceof EchoByteArray eba) {
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
