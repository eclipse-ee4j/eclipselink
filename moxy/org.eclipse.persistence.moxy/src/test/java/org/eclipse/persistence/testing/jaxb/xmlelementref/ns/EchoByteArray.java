/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//  - rbarkhouse - 17 November 2011 - 2.3.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmlelementref.ns;

import java.util.Arrays;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Marshaller;
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
