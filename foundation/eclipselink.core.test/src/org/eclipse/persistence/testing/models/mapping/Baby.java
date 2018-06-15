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
package org.eclipse.persistence.testing.models.mapping;

import java.io.*;
import java.math.*;

public class Baby implements Serializable {
    private BigDecimal id;
    private String name;
    private Crib crib;
    private BabyMonitor babyMonitor;

    public Baby() {
    }

    public static void addToDescriptor(org.eclipse.persistence.descriptors.ClassDescriptor desc) {
        desc.addConstraintDependencies(Crib.class);

    }

    public BabyMonitor getBabyMonitor() {
        return babyMonitor;
    }

    public Crib getCrib() {
        return crib;
    }

    public BigDecimal getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setBabyMonitor(BabyMonitor babyMonitor) {
        this.babyMonitor = babyMonitor;
    }

    public void setCrib(Crib crib) {
        this.crib = crib;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
