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

public class BabyMonitor implements Serializable {
    private BigDecimal id;
    private Baby baby;
    private Crib crib;
    private String brandName;

    public BabyMonitor() {
    }

    public Baby getBaby() {
        return baby;
    }

    public String getBrandName() {
        return brandName;
    }

    public Crib getCrib() {
        return crib;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setBaby(Baby baby) {
        this.baby = baby;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public void setCrib(Crib crib) {
        this.crib = crib;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }
}
