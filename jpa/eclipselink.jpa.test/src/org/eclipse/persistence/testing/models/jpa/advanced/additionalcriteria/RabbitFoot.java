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
//     15/08/2011-2.3.1 Guy Pelletier
//       - 298494: JPQL exists subquery generates unnecessary table join
package org.eclipse.persistence.testing.models.jpa.advanced.additionalcriteria;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="JPA_AC_RABBIT_FOOT")
public class RabbitFoot {
    @Id
    @GeneratedValue
    public int id;

    // FK, but user managed.
    @Column(name="RABBIT_ID")
    public int rabbitId;

    @Column(name="CAPTION")
    public String caption;

    public int getId() {
        return id;
    }

    public int getRabbitId() {
        return rabbitId;
    }

    public String getCaption() {
        return caption;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRabbitId(int rabbitId) {
        this.rabbitId = rabbitId;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
