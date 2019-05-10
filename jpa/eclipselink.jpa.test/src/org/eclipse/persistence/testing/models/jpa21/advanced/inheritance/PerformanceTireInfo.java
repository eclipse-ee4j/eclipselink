/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa21.advanced.inheritance;

import java.io.*;
import javax.persistence.*;

@Entity
@Table(name="JPA21_TIRE")
@DiscriminatorValue("Performance")
public class PerformanceTireInfo extends TireInfo<Integer> implements Serializable {
    protected Integer speedrating;

    public PerformanceTireInfo() {}

    @Column(name="SPEEDRATING")
    public Integer getSpeedRating() {
        return this.speedrating;
    }

    public void setSpeedRating(Integer rating) {
        this.speedrating = rating;
    }

}
