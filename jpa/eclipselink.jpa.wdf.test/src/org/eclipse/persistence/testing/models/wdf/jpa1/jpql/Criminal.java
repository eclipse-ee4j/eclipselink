/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2005, 2015 SAP. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     SAP - initial API and implementation

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
