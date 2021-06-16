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
//     15/08/2011-2.3.1 Guy Pelletier
//       - 298494: JPQL exists subquery generates unnecessary table join
package org.eclipse.persistence.testing.models.jpa.advanced.additionalcriteria;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

import static jakarta.persistence.FetchType.EAGER;

import org.eclipse.persistence.annotations.AdditionalCriteria;

@Entity
@Table(name="JPA_AC_EATER")
@DiscriminatorValue("E")
@PrimaryKeyJoinColumn(name="E_ID", referencedColumnName="P_ID")
@AdditionalCriteria("this.name like :EATER_NAME")
public class Eater extends Person {
    @Column(name="E_NAME")
    public String name;

    @OneToOne
    @JoinColumn(name="SANDWICH_ID")
    public Sandwich sandwhich;

    public String getName() {
        return name;
    }

    public Sandwich getSandwhich() {
        return sandwhich;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSandwhich(Sandwich sandwhich) {
        this.sandwhich = sandwhich;
    }
}
