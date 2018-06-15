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
package org.eclipse.persistence.testing.models.jpa.xml.inherited;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.AttributeOverride;

@AttributeOverride(name="id", column=@Column(name="CANADIAN_ID_INVALID", nullable=false))
public class Canadian extends Beer {
    public enum Flavor { LAGER, LIGHT, ICE, DRY }

    private Flavor flavor;
    private Date bornOnDate;

    public Canadian() {}

    public Date getBornOnDate() {
        return bornOnDate;
    }

    public Flavor getFlavor() {
        return flavor;
    }

    public void setBornOnDate(Date bornOnDate) {
        this.bornOnDate = bornOnDate;
    }

    public void setFlavor(Flavor flavor) {
        this.flavor = flavor;
    }
}
