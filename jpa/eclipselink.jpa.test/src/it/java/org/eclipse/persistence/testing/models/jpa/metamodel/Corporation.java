/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     06/30/2009-2.0  mobrien - finish JPA Metadata API modifications in support
//       of the Metamodel implementation for EclipseLink 2.0 release involving
//       Map, ElementCollection and Embeddable types on MappedSuperclass descriptors
//       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
package org.eclipse.persistence.testing.models.jpa.metamodel;

import static jakarta.persistence.CascadeType.ALL;

import java.util.Collection;
import java.util.HashSet;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@MappedSuperclass
@Access(AccessType.FIELD) // for 316991
public abstract class Corporation extends Person {

    // If a JoinTable with a JoinColumn is used - then we need a mappedBy on the inverse side here
    // Internally processed as a @ManyToMany because it is on a MappedSuperclass
    @OneToMany(cascade=ALL)//, mappedBy="corporation")
    //@JoinColumn(name="CORP_COMPUTERS")
    private Collection<Computer> corporateComputers = new HashSet<Computer>();

    @OneToOne // unidirectional
    private ArrayProcessor primarySuperComputer;

    public Collection<Computer> getCorporateComputers() {
        return corporateComputers;
    }

    public void setCorporateComputers(Collection<Computer> corporateComputers) {
        this.corporateComputers = corporateComputers;
    }

    public ArrayProcessor getPrimarySuperComputer() {
        return primarySuperComputer;
    }

    public void setPrimarySuperComputer(ArrayProcessor primarySuperComputer) {
        this.primarySuperComputer = primarySuperComputer;
    }


}
