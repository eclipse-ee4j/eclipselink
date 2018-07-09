/********************************************************************************
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 1998, 2018 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 * Contributors:
 *     07/09/2018-2.6 Jody Grassel
 *       - 536853: MapsID processing sets up to fail validation
 ******************************************************************************/

package org.eclipse.persistence.jpa.compositeid.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class CompBId implements Serializable {
    private RN rn;
    private CompAId compAId;
    private ClientId clientId;

    public RN getRN() {
        return rn;
    }
    public void setRN(RN rn) {
        this.rn = rn;
    }
    public CompAId getCompAId() {
        return compAId;
    }
    public void setCompAId(CompAId compAId) {
        this.compAId = compAId;
    }
    public ClientId getClientId() {
        return clientId;
    }
    public void setClientId(ClientId clientId) {
        this.clientId = clientId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((clientId == null) ? 0 : clientId.hashCode());
        result = prime * result + ((rn == null) ? 0 : rn.hashCode());
        result = prime * result + ((compAId == null) ? 0 : compAId.hashCode());
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
        CompBId other = (CompBId) obj;
        if (clientId == null) {
            if (other.clientId != null)
                return false;
        } else if (!clientId.equals(other.clientId))
            return false;
        if (rn == null) {
            if (other.rn != null)
                return false;
        } else if (!rn.equals(other.rn))
            return false;
        if (compAId == null) {
            if (other.compAId != null)
                return false;
        } else if (!compAId.equals(other.compAId))
            return false;
        return true;
    }


}
