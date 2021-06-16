/*
 * Copyright (c) 1998, 2020 Oracle and/or its affiliates. All rights reserved.
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

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Version;

@Entity
@NamedQueries({
  @NamedQuery(name = "Benefit.findAll", query = "select o from Benefit o")
})
@Inheritance
public abstract class Benefit implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "keygen")
    private Integer benefitId;
    @Version
    private Integer version;
    @OneToOne
    private Customer customer;

    String benefitDescription;

    public Benefit() {
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Integer getBenefitId() {
        return benefitId;
    }

    public void setBenefitId(Integer benefitId) {
        this.benefitId = benefitId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setBenefitDescription(String benefitDescription) {
        this.benefitDescription = benefitDescription;
    }

    public String getBenefitDescription() {
        return benefitDescription;
    }
}
