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

import org.eclipse.persistence.indirection.*;

public class Bartender {
    public int id;
    public String firstName;
    public String lastName;
    public ValueHolderInterface bar;
    public ValueHolderInterface qualification;

    public Bartender() {
        this.bar = new ValueHolder();
        this.qualification = new ValueHolder();
    }

    public Bar getBar() {
        return (Bar) bar.getValue();
    }

    public String getFirstName() {
        return firstName;
    }

    public int getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public Qualification getQualification() {
        return (Qualification) qualification.getValue();
    }

    public void setBar(Bar bar) {
        this.bar.setValue(bar);
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setQualification(Qualification qualification) {
        this.qualification.setValue(qualification);
    }
}
