/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith - August 2013
package org.eclipse.persistence.testing.jaxb.inheritance.id;

import javax.xml.bind.annotation.*;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class BImpl extends Base implements B {

    public BImpl() {
    }

    public BImpl(String id) {
        super.setId(id);
    }

    @XmlIDREF
    @XmlElement(type = AImpl.class)
    private A a;


    public A getA() {
        return a;
    }

    public void setA(A a) {
        this.a = a;
    }

    public boolean equals(Object obj){
        return (super.equals(obj));
    }

}
