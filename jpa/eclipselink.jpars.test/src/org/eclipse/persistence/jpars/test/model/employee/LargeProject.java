/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 ******************************************************************************/
package org.eclipse.persistence.jpars.test.model.employee;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.util.Calendar;

import javax.persistence.Basic;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;

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
}
