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
//     10/22/2009 - Guy Pelletier/Prakash Selvaraj - added tests for DDL generation of table per class
package org.eclipse.persistence.testing.models.jpa.ddlgeneration.tableperclass;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;

import java.io.Serializable;
import java.util.List;

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
