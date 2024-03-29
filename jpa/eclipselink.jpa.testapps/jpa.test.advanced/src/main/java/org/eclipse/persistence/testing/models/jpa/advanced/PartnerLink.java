/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.advanced;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.io.Serializable;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "MW")
@IdClass(org.eclipse.persistence.testing.models.jpa.advanced.PartnerLinkPK.class)
public class PartnerLink implements Serializable {
    private Man man;
    private Woman woman;

    public PartnerLink() {
    }

    @Id
    @OneToOne(cascade = PERSIST, fetch = LAZY)
    @JoinColumn(name = "M")
    public Man getMan() {
        return man;
    }

    @Transient
    public Integer getManId() {
        return (getMan() == null) ? null : getMan().getId();
    }

    @Id
    @OneToOne(cascade = PERSIST, fetch = LAZY)
    @JoinColumn(name = "W")
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
