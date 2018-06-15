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
//     10/22/2009 - Guy Pelletier/Prakash Selvaraj - added tests for DDL generation of table per class
package org.eclipse.persistence.testing.models.jpa.ddlgeneration.tableperclass;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
  @NamedQuery(name = "LuxuryCar.findAll", query = "select o from LuxuryCar o")
})
@Inheritance
@DiscriminatorValue("LUXURY")
public class LuxuryCar extends Car implements Serializable {
    String interiorDescription;

    Float bootSpace;

    public LuxuryCar() {}

    public void setInteriorDescription(String interiorDescription) {
        this.interiorDescription = interiorDescription;
    }

    public String getInteriorDescription() {
        return interiorDescription;
    }

    public void setBootSpace(Float bootSpace) {
        this.bootSpace = bootSpace;
    }

    public Float getBootSpace() {
        return bootSpace;
    }
}
