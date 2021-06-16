/*
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.models.wdf.jpa1.jpql;

import java.util.Collection;
import java.util.Set;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "TMP_COP")
public class Cop {
    @Id
    protected long id;
    @Embedded
    @AttributeOverrides( { @AttributeOverride(name = "integer", column = @Column(name = "COP_TESLA_INT")),
            @AttributeOverride(name = "sound", column = @Column(name = "COP_TESLA_BLOB")) })
    protected Tesla tesla;

    @OneToMany
    protected Collection<Criminal> attachedCriminals;

    @OneToOne
    protected Cop partner;

    @ManyToMany
    protected Set<Informer> informers;

    public Collection<Criminal> getAttachedCriminals() {
        return attachedCriminals;
    }

    public void setAttachedCriminals(Collection<Criminal> attachedCriminals) {
        this.attachedCriminals = attachedCriminals;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Set<Informer> getInformers() {
        return informers;
    }

    public void setInformers(Set<Informer> informers) {
        this.informers = informers;
    }

    public Cop getPartner() {
        return partner;
    }

    public void setPartner(Cop partner) {
        this.partner = partner;
    }

    public Tesla getTesla() {
        return tesla;
    }

    public void setTesla(Tesla tesla) {
        this.tesla = tesla;
    }
}
