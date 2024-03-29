/*
 * Copyright (c) 2018, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.jaxb.xmlelementref.nills;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Root {

    @XmlElementRef(name="foo")
    protected JAXBElement<String> foo;

    @XmlElementRef(name="bar")
    protected JAXBElement<String> bar;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Root r))
            return false;
        return isEqual(foo, r.foo) && isEqual(bar, r.bar);
    }

    private boolean isEqual(JAXBElement<?> e1, JAXBElement<?> e2) {
        if (e1 != null && e2 != null) {
            boolean result =  e1.getName().equals(e2.getName()) &&
                    e1.getDeclaredType().equals(e2.getDeclaredType()) &&
                    (e1.isNil() == e2.isNil());
            if (e1.getValue() != null)
                result = result && e1.getValue().equals(e2.getValue());
            else
                result = result && (e2.getValue() == null);
            return result;
        } else {
            return (e1 == null) && (e2 == null);
        }
    }

}
