/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.advanced;

import java.io.Serializable;

import jakarta.persistence.*;

public class PartnerLinkPK implements Serializable {
    private int manId;
    private Integer womanId;

    public PartnerLinkPK() {}

       public PartnerLinkPK(int manId, Integer womanId) {
           this.manId = manId;
           this.womanId = womanId;
       }


    @Id
    public int getMan() {
        return manId;
    }

    @Id
    public Integer getWoman() {
        return womanId;
    }

    public void setMan(int manId) {
        this.manId = manId;
    }

    public void setWoman(Integer womanId) {
        this.womanId = womanId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + manId;
        result = prime * result + ((womanId == null) ? 0 : womanId.hashCode());
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
        if (manId != other.manId)
            return false;
        if (womanId == null) {
            if (other.womanId != null)
                return false;
        } else if (!womanId.equals(other.womanId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PartnerLinkPK [manId=" + manId + ", womanId=" + womanId + "]";
    }
}
