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
package org.eclipse.persistence.testing.tests.optimisticlocking.cascaded;

import java.util.Vector;
import org.eclipse.persistence.indirection.*;

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
        getAwards().addElement(award);
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
        return ((Vector) awards.getValue()).size() > 0;
    }

    public void removeAward(Award award) {
        getAwards().removeElement(award);
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
