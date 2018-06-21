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

import javax.persistence.AssociationOverride;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

@Entity
public class PatentInvestigator {
    @Id
    private int id;

    @Embedded
    @AttributeOverride(name="description", column=@Column(name = "LAST_DESRIPTION"))
    @AssociationOverride(name="patent", joinColumns=@JoinColumn(name = "LAST_PATENT"))
    private PatentInvestigation last;

    @Embedded
    @AttributeOverride(name="description", column=@Column(name = "CURRENT_DESRIPTION"))
    @AssociationOverride(name="patent", joinColumns=@JoinColumn(name = "CURRENT_PATENT"))
    private PatentInvestigation current;

    @Embedded
    @AttributeOverride(name="description", column=@Column(name = "NEXT_DESRIPTION"))
    @AssociationOverride(name="patent", joinColumns=@JoinColumn(name = "NEXT_PATENT"))
    private PatentInvestigation next;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public PatentInvestigation getLast() {
        return last;
    }
    public void setLastCompleted(PatentInvestigation last) {
        this.last = last;
    }
    public PatentInvestigation getCurrent() {
        return current;
    }
    public void setCurrent(PatentInvestigation current) {
        this.current = current;
    }
    public PatentInvestigation getNext() {
        return next;
    }
    public void setNextPlanned(PatentInvestigation next) {
        this.next = next;
    }
}
