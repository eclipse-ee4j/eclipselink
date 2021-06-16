/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     10/01/2018: Will Dazey
//       - #253: Add support for embedded constructor results with CriteriaBuilder
package org.eclipse.persistence.jpa.test.criteria.model;

import jakarta.persistence.*;

@Entity
public class L1 {

    @Id @Column(name="ID")
    private Integer id;
    
    @Column(name="NAME")
    private String name;

    @ManyToOne
    @JoinColumn(name="ID_L2", referencedColumnName="ID")
    private L2 l2;

    public L1() {}

    public L1(Integer id, String name, L2 l2) {
        this.id = id;
        this.name = name;
        this.l2 = l2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public L2 getL2() {
        return l2;
    }

    public void setL2(L2 l2) {
        this.l2 = l2;
    }
}
