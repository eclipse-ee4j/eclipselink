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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.inheritance;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "e-type")
@XmlAccessorType(XmlAccessType.FIELD)
public class E extends D {
    private int eee;

    public int getEee() {
        return eee;
    }

    public void setEee(int eee) {
        this.eee = eee;
    }

    public boolean equals(Object obj) {
        if(!(obj instanceof E)) {
            return false;
        }
        E objE = (E)obj;
        if(getEee() != objE.getEee()){
            return false;
        }
        if(getDdd() != objE.getDdd()){
            return false;
        }
        if(getCcc() != objE.getCcc()){
            return false;
        }
        if(getFoo() != objE.getFoo()){
            return false;
        }
        if(getBbb() != objE.getBbb()){
            return false;
        }
        if(getAaa() != objE.getAaa()){
            return false;
        }

        return true;
    }
}
