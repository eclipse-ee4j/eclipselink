/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

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
