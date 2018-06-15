/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.jaxb.xmlelementref.nills;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Root {

    @XmlElementRef(name="foo")
    protected JAXBElement<String> foo;

    @XmlElementRef(name="bar")
    protected JAXBElement<String> bar;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Root))
            return false;
        Root r = (Root) obj;
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
