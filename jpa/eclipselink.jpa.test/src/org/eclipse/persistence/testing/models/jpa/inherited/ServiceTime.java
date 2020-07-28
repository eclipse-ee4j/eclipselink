/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     06/02/2009-2.0 Guy Pelletier
//       - 278768: JPA 2.0 Association Override Join Table
//     02/18/2010-2.0.2 Guy Pelletier
//       - 294803: @Column(updatable=false) has no effect on @Basic mappings
//     09/16/2010-2.2 Guy Pelletier
//       - 283028: Add support for letting an @Embeddable extend a @MappedSuperclass
package org.eclipse.persistence.testing.models.jpa.inherited;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@AttributeOverrides({
    @AttributeOverride(name="endDate", column=@Column(name="END_DATE", insertable=false, updatable=true)),
    @AttributeOverride(name="startDate", column=@Column(name="START_DATE", updatable=false))
})
public class ServiceTime extends TrackableTime {
    public ServiceTime() {}

    public String toString() {
        return "ServiceTime: " + "[" + getStartDate() + "] - [" + getEndDate() + "]";
    }
}

