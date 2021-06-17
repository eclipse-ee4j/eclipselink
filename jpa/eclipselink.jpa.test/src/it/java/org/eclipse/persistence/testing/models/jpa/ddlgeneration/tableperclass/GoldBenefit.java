/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;

@Entity
@NamedQueries({
  @NamedQuery(name = "GoldBenefit.findAll", query = "select o from GoldBenefit o")
})
@Inheritance
@DiscriminatorValue("GOLD")
public class GoldBenefit extends Benefit implements Serializable
{

    @OneToOne
    private LuxuryCar luxuryCar;

    public GoldBenefit() {
    }

    public LuxuryCar getLuxuryCar() {
        return luxuryCar;
    }

    public void setLuxuryCar(LuxuryCar luxuryCar) {
        this.luxuryCar = luxuryCar;
    }
}
