/*
 * Copyright (c) 2014, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Martin Vojtek - 2.6.0 - initial implementation
package org.eclipse.persistence.testing.jaxb.objectgraph;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Elements {
    @XmlElement
    Object element1;
    @XmlElement
    Object element2;
    @XmlElement
    Object element3;
    public Object getElement1() {
        return element1;
    }
    public void setElement1(Object element1) {
        this.element1 = element1;
    }
    public Object getElement2() {
        return element2;
    }
    public void setElement2(Object element2) {
        this.element2 = element2;
    }
    public Object getElement3() {
        return element3;
    }
    public void setElement3(Object element3) {
        this.element3 = element3;
    }
}
