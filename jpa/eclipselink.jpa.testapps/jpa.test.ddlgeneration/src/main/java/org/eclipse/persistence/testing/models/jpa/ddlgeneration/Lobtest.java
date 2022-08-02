/*
 * Copyright (c) 2011, 2022 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2011, 2022 Kristian Rye Vennesland. All rights reserved.
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
//     01/11/2011-2.3 Guy Pelletier submitted for Kristian Rye Vennesland
//       - 277079: EmbeddedId's fields are null when using LOB with fetchtype LAZY
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.math.BigInteger;

/**
 *
 * @author Kristian
 */
@Entity
@Table(name = "LOBTEST")
@NamedQueries({
    @NamedQuery(name = "Lobtest.findAll", query = "SELECT l FROM Lobtest l"),
    @NamedQuery(name = "Lobtest.findByVersionid", query = "SELECT l FROM Lobtest l WHERE l.lobtestPK.versionid = :versionid"),
    @NamedQuery(name = "Lobtest.findByUuid", query = "SELECT l FROM Lobtest l WHERE l.uuid = :uuid"),
    @NamedQuery(name = "Lobtest.findByDocid", query = "SELECT l FROM Lobtest l WHERE l.lobtestPK.docid = :docid")})
public class Lobtest implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected LobtestPK lobtestPK;

    @Basic(optional = false)
    @Column(name = "UUID")
    private String uuid;

    @Lob
    @Basic(fetch=FetchType.LAZY)
    @Column(name = "CONTENTDATA")
    private byte[] contentdata;

    public Lobtest() {
    }

    public Lobtest(LobtestPK lobtestPK) {
        this.lobtestPK = lobtestPK;
    }

    public Lobtest(LobtestPK lobtestPK, String uuid) {
        this.lobtestPK = lobtestPK;
        this.uuid = uuid;
    }

    public Lobtest(BigInteger versionid, String docid) {
        this.lobtestPK = new LobtestPK(versionid, docid);
    }

    public LobtestPK getLobtestPK() {
        return lobtestPK;
    }

    public void setLobtestPK(LobtestPK lobtestPK) {
        this.lobtestPK = lobtestPK;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public byte[] getContentdata() {
        return contentdata;
    }

    public void setContentdata(byte[] contentdata) {
        this.contentdata = contentdata;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (lobtestPK != null ? lobtestPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Lobtest)) {
            return false;
        }
        Lobtest other = (Lobtest) object;
        if ((this.lobtestPK == null && other.lobtestPK != null) || (this.lobtestPK != null && !this.lobtestPK.equals(other.lobtestPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "no.wstech.portalpublisher.ejb.entity.Lobtest[lobtestPK=" + lobtestPK + "]";
    }
}
