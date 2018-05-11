/*******************************************************************************
 * Copyright (c) 2018 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     07/09/2018-2.6 Jody Grassel
 *       - 536853: MapsID processing sets up to fail validation
 ******************************************************************************/

package org.eclipse.persistence.jpa.compositeid.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class CompCId implements Serializable {
    private BTI bti;
    private UserId userId;
    private CompBId compBId;

    public BTI getBTI() {
        return bti;
    }
    public void setBTI(BTI bti) {
        this.bti = bti;
    }
    public UserId getUserId() {
        return userId;
    }
    public void setUserId(UserId userId) {
        this.userId = userId;
    }
    public CompBId getCompBIdId() {
        return compBId;
    }
    public void setCompBIdId(CompBId compBId) {
        this.compBId = compBId;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bti == null) ? 0 : bti.hashCode());
        result = prime * result + ((compBId == null) ? 0 : compBId.hashCode());
        result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
        CompCId other = (CompCId) obj;
        if (bti == null) {
            if (other.bti != null)
                return false;
        } else if (!bti.equals(other.bti))
            return false;
        if (compBId == null) {
            if (other.compBId != null)
                return false;
        } else if (!compBId.equals(other.compBId))
            return false;
        if (userId == null) {
            if (other.userId != null)
                return false;
        } else if (!userId.equals(other.userId))
            return false;
        return true;
    }


}
