/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     01/11/2011-2.3 Guy Pelletier submitted for Kristian Rye Vennesland
//       - 277079: EmbeddedId's fields are null when using LOB with fetchtype LAZY
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import java.io.Serializable;
import java.math.BigInteger;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 *
 * @author Kristian
 */
@Embeddable
public class LobtestPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "VERSIONID")
    private BigInteger versionid;

    @Basic(optional = false)
    @Column(name = "DOCID")
    private String docid;

    public LobtestPK() {}

    public LobtestPK(BigInteger versionid, String docid) {
        this.versionid = versionid;
        this.docid = docid;
    }

    public BigInteger getVersionid() {
        return versionid;
    }

    public void setVersionid(BigInteger versionid) {
        this.versionid = versionid;
    }

    public String getDocid() {
        return docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (versionid != null ? versionid.hashCode() : 0);
        hash += (docid != null ? docid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LobtestPK)) {
            return false;
        }
        LobtestPK other = (LobtestPK) object;
        if ((this.versionid == null && other.versionid != null) || (this.versionid != null && !this.versionid.equals(other.versionid))) {
            return false;
        }
        if ((this.docid == null && other.docid != null) || (this.docid != null && !this.docid.equals(other.docid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "LobtestPK[versionid=" + versionid + ", docid=" + docid + "]";
    }

}
