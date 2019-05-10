/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
//     tware - add for testing JPA 2.0 delimited identifiers
package org.eclipse.persistence.testing.models.jpa.delimited;

import javax.persistence.*;

import org.eclipse.persistence.annotations.ExistenceChecking;

import static org.eclipse.persistence.annotations.ExistenceType.ASSUME_NON_EXISTENCE;

/**
 * LargeProject subclass of Project.
 * This class in used to test inheritance.
 * The field names intentionally do not match the property names to test method weaving.
 */
@Entity
@Table(name="CMP3_DEL_LPROJECT")
@DiscriminatorValue("L")
@NamedQueries({
@NamedQuery(
    name="findWithBudgetLargerThan",
    query="SELECT OBJECT(p) FROM LargeProject p WHERE p.budget >= :amount"
),
@NamedQuery(
    name="constructLProject",
    query="SELECT new org.eclipse.persistence.testing.models.jpa.delimited.LargeProject(p.name) FROM LargeProject p")
}
)
@ExistenceChecking(ASSUME_NON_EXISTENCE)
public class LargeProject extends Project {
    private double m_budget;

    public LargeProject() {
        super();
    }

    public LargeProject(String name) {
        this();
        this.setName(name);
    }

    public double getBudget() {
        return m_budget;
    }

    public void setBudget(double budget) {
        this.m_budget = budget;
    }

    /**
     * This tests over-writing a get method.
     */
    public Employee getTeamLeader() {
        return m_teamLeader;
    }
}
