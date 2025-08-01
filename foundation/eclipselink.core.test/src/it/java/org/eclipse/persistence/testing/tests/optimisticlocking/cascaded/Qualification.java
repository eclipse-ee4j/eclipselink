/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.optimisticlocking.cascaded;

import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;

import java.util.Vector;

public class Qualification {
    public int id;
    public int version;
    public int yearsOfExperience;
    public ValueHolderInterface awards;

    public Qualification() {
        this.awards = new ValueHolder(new Vector());
    }

    public Vector getAwards() {
        return (Vector) awards.getValue();
    }

    public void addAward(Award award) {
        getAwards().add(award);
        award.setQualification(this);
    }

    public int getId() {
        return this.id;
    }

    public int getVersion() {
        return this.version;
    }

    public int getYearsOfExperience() {
        return this.yearsOfExperience;
    }

    public boolean hasAwards() {
        return !((Vector) awards.getValue()).isEmpty();
    }

    public void removeAward(Award award) {
        getAwards().remove(award);
    }

    public void setAwards(Vector awards) {
        this.awards.setValue(awards);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setYearsOfExperience(int yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }
}
