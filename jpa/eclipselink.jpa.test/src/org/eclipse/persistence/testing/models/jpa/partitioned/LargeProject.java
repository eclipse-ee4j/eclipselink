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
package org.eclipse.persistence.testing.models.jpa.partitioned;

import javax.persistence.*;

/**
 * LargeProject subclass of Project.
 * This class in used to test inheritance.
 * The field names intentionally do not match the property names to test method weaving.
 */
@Entity
@Table(name="PART_LPROJECT")
@DiscriminatorValue("L")
public class LargeProject extends Project {
    private double budget;

    public LargeProject() {
        super();
    }

    public LargeProject(String name) {
        this();
        setName(name);
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    /**
     * This tests over-writing a get method.
     */
    public Employee getTeamLeader() {
        return teamLeader;
    }
}
