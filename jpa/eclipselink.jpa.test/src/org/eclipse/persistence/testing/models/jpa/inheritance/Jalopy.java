/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     tware - testing for query downcasting
package org.eclipse.persistence.testing.models.jpa.inheritance;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="CMP3_JALOPY")
@DiscriminatorValue("J")
public class Jalopy extends Car {

    protected int percentRust = 0;

    public int getPercentRust() {
        return percentRust;
    }

    public void setPercentRust(int percentRust) {
        this.percentRust = percentRust;
    }


}
