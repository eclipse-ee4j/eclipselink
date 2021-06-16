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
package org.eclipse.persistence.testing.models.jpa.complexaggregate;

import jakarta.persistence.*;
import java.io.Serializable;

@Embeddable
public class PersonalVitals implements Serializable {
    private int age;
    private double weight;
    private double height;

    public PersonalVitals() {}

    @Column(name="AGE")
    public int getAge() {
        return age;
    }

    @Column(name="WEIGHT")
    public double getWeight() {
        return weight;
    }

    @Column(name="HEIGHT")
    public double getHeight() {
        return height;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String toString() {
        return "Personal Vitals: " + getWeight() + " lbs,  " + getHeight() + " meters, " + getAge() + " years old";
    }
}
