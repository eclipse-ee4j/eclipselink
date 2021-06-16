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
