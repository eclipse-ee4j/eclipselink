/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     ailitche - testing for embedded with FK OneToMany
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import jakarta.persistence.Embeddable;

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
