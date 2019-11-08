/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     ailitche - testing for embedded with FK OneToMany
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import javax.persistence.Embeddable;

@Embeddable
public class PatentInvestigation {
    private String description;
    private Patent patent;

    public PatentInvestigation() {
        super();
    }
    public PatentInvestigation(Patent patent) {
        this.patent = patent;
    }
    public PatentInvestigation(String description, Patent patent) {
        this.description = description;
        this.patent = patent;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Patent getPatent() {
        return this.patent;
    }
    public void setPatent(Patent patent) {
        this.patent = patent;
    }

}
