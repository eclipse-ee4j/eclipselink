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
//     06/02/2009-2.0 Guy Pelletier
//       - 278768: JPA 2.0 Association Override Join Table
//     02/18/2010-2.0.2 Guy Pelletier
//       - 294803: @Column(updatable=false) has no effect on @Basic mappings
package org.eclipse.persistence.testing.models.jpa.xml.inherited;

import javax.persistence.Embeddable;

@Embeddable
public class ServiceTime {
    private String startDate;
    private String endDate;

    public ServiceTime() {}

    public String getEndDate() {
        return endDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setStartDate(String endDate) {
        this.startDate = endDate;
    }

    public String toString() {
        return "ServiceTime: " + "[" + startDate + "] - [" + endDate + "]";
    }
}
