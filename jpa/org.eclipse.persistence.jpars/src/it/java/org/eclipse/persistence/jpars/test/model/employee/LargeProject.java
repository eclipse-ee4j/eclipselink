/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.jpars.test.model.employee;

import static jakarta.persistence.TemporalType.TIMESTAMP;

import java.util.Calendar;

import jakarta.persistence.Basic;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;

@Entity
@Table(name = "JPARS_LPROJECT")
@DiscriminatorValue("L")
public class LargeProject extends Project {
    @Basic
    private double budget;
    @Basic
    @Temporal(TIMESTAMP)
    private Calendar milestone = Calendar.getInstance();

    public LargeProject() {
        super();
    }

    public double getBudget() {
        return this.budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public Calendar getMilestone() {
        return milestone;
    }

    public void setMilestone(Calendar milestone) {
        this.milestone = milestone;
    }

    @Override
    public String toString() {
        return "budget=" + budget + ", milestone=" + milestone;
    }
}
