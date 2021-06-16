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
//     10/22/2009 - Guy Pelletier/Prakash Selvaraj - added tests for DDL generation of table per class
package org.eclipse.persistence.testing.models.jpa.ddlgeneration.tableperclass;

import java.io.Serializable;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;

@Entity
@NamedQueries({
  @NamedQuery(name = "GoldCustomer.findAll", query = "select o from GoldCustomer o")
})
public class GoldCustomer extends Customer implements Serializable {
    @OneToMany
    @JoinTable(
      name="GoldCustomer_Benefit",
      joinColumns=@JoinColumn(name="CUSTOMER_ID")
    )
    private List<GoldBenefit> goldBenefitList;

    public GoldCustomer() {
    }

    public List<GoldBenefit> getGoldBenefitList() {
        return goldBenefitList;
    }

    public void setGoldBenefitList(List<GoldBenefit> goldBenefitList) {
        this.goldBenefitList = goldBenefitList;
    }
}
