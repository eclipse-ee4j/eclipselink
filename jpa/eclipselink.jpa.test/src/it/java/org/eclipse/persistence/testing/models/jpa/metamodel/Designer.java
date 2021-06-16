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

import static jakarta.persistence.FetchType.EAGER;

//import jakarta.persistence.Column;
//import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToOne;

@MappedSuperclass
public abstract class Designer extends Person {

/*    @Id
    // InstanceVariableAttributeAccessor testing
    @Column(name="DESIGNER_ID")
    private Integer ident;
*/

    // The M:1 side is the owning side - but this is a unidirectional mapping
    // The ManyToOne will resolve to a OneToOne internally without a unique PK restriction
    @ManyToOne(fetch=EAGER)//LAZY)
    @JoinColumn(name="SEC_EMPLOYER_PERSON_ID")
    /*
    @JoinTable(name="CMP3_MM_MANUF_MM_DESIGNER",
                joinColumns = @JoinColumn(name="PERSON_ID"),
                inverseJoinColumns =@JoinColumn(name="MANUF_ID"))*/
    private Manufacturer secondaryEmployer;

    // Unidirectional OneToOne
    @OneToOne
    @JoinColumn(name="PRIME_EMPLOYER_PERSON_ID")
    private Manufacturer primaryEmployer;

    public Manufacturer getPrimaryEmployer() {
        return primaryEmployer;
    }

    public void setPrimaryEmployer(Manufacturer primaryEmployer) {
        this.primaryEmployer = primaryEmployer;
    }

    public Manufacturer getSecondaryEmployer() {
        return secondaryEmployer;
    }

    public void setSecondaryEmployer(Manufacturer employer) {
        this.secondaryEmployer = employer;
    }
}
