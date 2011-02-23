/*******************************************************************************
 * Copyright (c) 2011 Kristian Rye Vennesland. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     01/11/2011-2.1.3 Guy Pelletier submitted for Kristian Rye Vennesland  
 *       - 277079: EmbeddedId's fields are null when using LOB with fetchtype LAZY
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import java.io.Serializable;
import java.math.BigInteger;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

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
