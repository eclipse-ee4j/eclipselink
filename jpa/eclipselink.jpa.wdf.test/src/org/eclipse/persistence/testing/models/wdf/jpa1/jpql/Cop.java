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
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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
