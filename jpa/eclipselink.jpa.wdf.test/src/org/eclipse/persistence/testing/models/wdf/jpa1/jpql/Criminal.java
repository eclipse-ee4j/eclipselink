/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.models.wdf.jpa1.jpql;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "TMP_CRIMINAL")
public class Criminal {
    public enum CriminalType {
        NICE, MEAN, UGLY, HARMFUL
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "TRANSMISSION_TYPE")
    protected CriminalType cType;

    @Id
    protected long id;

    @ManyToOne
    protected Cop attachedCop;

    @OneToMany
    protected Collection<Criminal> parties;

    public Criminal() {
        this.cType = CriminalType.HARMFUL;
    }

    public Cop getAttachedCop() {
        return attachedCop;
    }

    public void setAttachedCop(Cop attachedCop) {
        this.attachedCop = attachedCop;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Collection<Criminal> getParties() {
        return parties;
    }

    public void setParties(Collection<Criminal> parties) {
        this.parties = parties;
    }

    public CriminalType getCType() {
        return cType;
    }

    public void setCType(CriminalType type) {
        cType = type;
    }

}
