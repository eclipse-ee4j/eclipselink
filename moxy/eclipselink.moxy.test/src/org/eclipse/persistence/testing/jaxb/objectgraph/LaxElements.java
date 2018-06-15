/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Martin Vojtek - 2.6.0 - initial implementation
package org.eclipse.persistence.testing.jaxb.objectgraph;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class LaxElements {
    @XmlElement
    Object element4;
    @XmlElement
    Object element5;
    @XmlElement
    Object element6;
    public Object getElement4() {
        return element4;
    }
    public void setElement4(Object element4) {
        this.element4 = element4;
    }
    public Object getElement5() {
        return element5;
    }
    public void setElement5(Object element5) {
        this.element5 = element5;
    }
    public Object getElement6() {
        return element6;
    }
    public void setElement6(Object element6) {
        this.element6 = element6;
    }


}
