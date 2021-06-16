/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     tware - add for testing JPA 2.0 delimited identifiers
package org.eclipse.persistence.testing.models.jpa.delimited;

import jakarta.persistence.*;

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
    query="SELECT OBJECT(project) FROM LargeProject project WHERE project.budget >= :amount"
),
@NamedQuery(
    name="constructLProject",
    query="SELECT new org.eclipse.persistence.testing.models.jpa.delimited.LargeProject(project.name) FROM LargeProject project")
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
