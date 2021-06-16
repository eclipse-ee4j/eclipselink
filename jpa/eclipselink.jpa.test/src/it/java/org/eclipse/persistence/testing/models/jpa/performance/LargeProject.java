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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.performance;

import java.io.*;
import java.sql.Timestamp;

/**
 * <b>Purpose</b>: Larger scale projects within the Employee Demo
 * <p><b>Description</b>: LargeProject is a concrete subclass of Project. It is instantiated for Projects with type = 'L'. The additional
 * information (budget, & milestoneVersion) are mapped from the LPROJECT table.
 * @see Project
 */
//@org.hibernate.annotations.Cache(usage=org.hibernate.annotations.CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class LargeProject extends Project {
    protected double budget;
    protected Timestamp milestoneVersion;

    public LargeProject() {
        this.budget = 0.0;
    }

    public double getBudget() {
        return budget;
    }

    public Timestamp getMilestoneVersion() {
        return milestoneVersion;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public void setMilestoneVersion(Timestamp milestoneVersion) {
        this.milestoneVersion = milestoneVersion;
    }

    /**
     * Print the project's data.
     */
    public String toString() {
        StringWriter writer = new StringWriter();

        writer.write("Large Project: ");
        writer.write(getName());
        writer.write(" ");
        writer.write(getDescription());
        writer.write(" " + getBudget());
        writer.write(" ");
        writer.write(String.valueOf(getMilestoneVersion()));
        return writer.toString();
    }
}
