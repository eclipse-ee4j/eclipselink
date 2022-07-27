/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import org.eclipse.persistence.annotations.CascadeOnDelete;

/**
 * Local interface for the large project bean.
 * This is the bean's public/local interface for the clients usage.
 * All locals must extend the jakarta.ejb.EJBLocalObject.
 * The bean itself does not have to implement the local interface, but must implement all of the methods.
 */
@Entity(name="LargeProject")
@Table(name="CMP3_FA_LPROJECT")
@CascadeOnDelete
@DiscriminatorValue("L")
@NamedQueries({
@NamedQuery(
    name="findFieldAccessWithBudgetLargerThan",
    query="SELECT OBJECT(project) FROM LargeProject project WHERE project.budget >= :amount"
),
@NamedQuery(
    name="constructFieldAccessLProject",
    query="SELECT new org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.LargeProject(project.name) FROM LargeProject project")
}
)
public class LargeProject extends Project {
    private double budget;

    public LargeProject () {
        super();
        fieldOnlySetThroughConstructor = 2;
    }

    public LargeProject (String name) {
        this();
        this.setName(name);
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
            this.budget = budget;
    }

    /**
     * This tests accessing a superclass field.
     */
    @Override
    public Employee getTeamLeader() {
        return teamLeader;
    }
}
