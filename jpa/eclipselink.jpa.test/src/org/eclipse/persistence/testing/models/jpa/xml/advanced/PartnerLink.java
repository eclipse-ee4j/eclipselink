/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.xml.advanced;

import javax.persistence.Transient;
//@Entity
//@Table(name="MW")
//@IdClass(org.eclipse.persistence.testing.models.jpa.xml.advanced.PartnerLinkPK.class)
public class PartnerLink {
    private Man man;
    private Woman woman;

    public PartnerLink() {}
    //@Id
    //@OneToOne(cascade=PERSIST)
    //@JoinColumn(name="M")
    public Man getMan() {
        return man;
    }
    @Transient
    public Integer getManId() {
        return (getMan() == null) ? null : getMan().getId();
    }

    //@Id
    //@OneToOne(cascade=PERSIST)
    //@JoinColumn(name="W")
    public Woman getWoman() {
        return woman;
    }

    @Transient
    public Integer getWomanId() {
        return (getWoman() == null) ? null : getWoman().getId();
    }

    public void setMan(Man man) {
        this.man = man;
    }

    public void setManId(Integer manId) {
    }

    public void setWoman(Woman woman) {
        this.woman = woman;
    }

    public void setWomanId(Integer womanId) {
    }
}
