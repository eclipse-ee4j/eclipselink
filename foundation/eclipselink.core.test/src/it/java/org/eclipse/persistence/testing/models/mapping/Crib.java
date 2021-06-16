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
package org.eclipse.persistence.testing.models.mapping;

import java.io.*;
import java.math.*;

public class Crib implements Serializable {
    private Baby baby;
    private String color;
    private BigDecimal id;
    private BabyMonitor babyMonitor;

    public Crib() {
    }

    public static void addToDescriptor(org.eclipse.persistence.descriptors.ClassDescriptor desc) {
        desc.addConstraintDependencies(BabyMonitor.class);

    }

    public Baby getBaby() {
        return baby;
    }

    public BabyMonitor getBabyMonitor() {
        return babyMonitor;
    }

    public String getColor() {
        return color;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setBaby(Baby baby) {
        this.baby = baby;
    }

    public void setBabyMonitor(BabyMonitor babyMonitor) {
        this.babyMonitor = babyMonitor;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }
}
