/*
 * Copyright (c) 2022 IBM and/or its affiliates. All rights reserved.
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
//     IBM - initial API and implementation

package org.eclipse.persistence.jpa.test.uuid.model;

import java.util.UUID;

/**
 *
 */
public class XMLEmbeddableUUID_ID {
    private UUID eid;

    public XMLEmbeddableUUID_ID() {

    }

    public XMLEmbeddableUUID_ID(UUID id) {
        this.eid = id;
    }

    /**
     * @return the id
     */
    public UUID getEId() {
        return eid;
    }

    /**
     * @param id the id to set
     */
    public void setEId(UUID id) {
        this.eid = id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((eid == null) ? 0 : eid.hashCode());
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
        XMLEmbeddableUUID_ID other = (XMLEmbeddableUUID_ID) obj;
        if (eid == null) {
            if (other.eid != null)
                return false;
        } else if (!eid.equals(other.eid))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "XMLEmbeddableUUID_ID [eid=" + eid + "]";
    }

}