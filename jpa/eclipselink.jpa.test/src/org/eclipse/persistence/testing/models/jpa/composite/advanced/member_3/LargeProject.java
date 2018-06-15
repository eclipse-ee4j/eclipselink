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
package org.eclipse.persistence.testing.models.jpa.composite.advanced.member_3;

import javax.persistence.*;

import org.eclipse.persistence.annotations.ExistenceChecking;
import org.eclipse.persistence.testing.models.jpa.composite.advanced.member_2.Employee;

import static org.eclipse.persistence.annotations.ExistenceType.ASSUME_NON_EXISTENCE;

/**
 * LargeProject subclass of Project.
 * This class in used to test inheritance.
 * The field names intentionally do not match the property names to test method weaving.
 */
@Entity
@Table(name="MBR3_LPROJECT")
@DiscriminatorValue("L")
@NamedQueries({
@NamedQuery(
    name="findWithBudgetLargerThan",
    query="SELECT OBJECT(project) FROM LargeProject project WHERE project.budget >= :amount"
),
@NamedQuery(
    name="constructLProject",
    query="SELECT new org.eclipse.persistence.testing.models.jpa.composite.advanced.member_3.LargeProject(project.name) FROM LargeProject project")
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
