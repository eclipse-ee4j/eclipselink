/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.advanced;

import java.io.Serializable;

import javax.persistence.*;

public class PartnerLinkPK implements Serializable {
    private int man;
    private Integer woman;

    public PartnerLinkPK() {}

       public PartnerLinkPK(int man, Integer woman) {
           this.man = man;
           this.woman = woman;
       }

    @Id
    public int getMan() {
        return man;
    }

    @Id
    public Integer getWoman() {
        return woman;
    }

    public void setMan(int man) {
        this.man = man;
    }

    public void setWoman(Integer woman) {
        this.woman = woman;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + man;
        result = prime * result + ((woman == null) ? 0 : woman.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PartnerLinkPK other = (PartnerLinkPK) obj;
        if (man != other.man)
            return false;
        if (woman == null) {
            if (other.woman != null)
                return false;
        } else if (!woman.equals(other.woman))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PartnerLinkPK [manId=" + man + ", womanId=" + woman + "]";
    }
}
