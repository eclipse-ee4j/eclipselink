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
//     04/28/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 6)
package org.eclipse.persistence.testing.models.jpa.ddlgeneration.multitenant;

import java.util.Collection;
import java.util.Vector;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import static jakarta.persistence.CascadeType.ALL;

@Entity
@DiscriminatorValue("SOLDIER")
@Table(name="DDL_SOLDIER")
public class Soldier extends Mafioso {
    private Capo capo;
    private Collection<Contract> contracts;

    public Soldier() {
        this.contracts = new Vector<Contract>();
    }

    public void addContract(Contract contract) {
        contracts.add(contract);
    }

    @ManyToOne
    public Capo getCapo(){
        return capo;
    }

    @ManyToMany(cascade=ALL, mappedBy="soldiers")
    public Collection<Contract> getContracts() {
        return contracts;
    }

    public void setCapo(Capo capo){
        this.capo = capo;
    }

    public void setContracts(Collection<Contract> contracts) {
        this.contracts = contracts;
    }
}
