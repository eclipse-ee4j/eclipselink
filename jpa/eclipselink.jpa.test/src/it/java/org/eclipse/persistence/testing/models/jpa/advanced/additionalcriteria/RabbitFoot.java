/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     15/08/2011-2.3.1 Guy Pelletier
//       - 298494: JPQL exists subquery generates unnecessary table join
package org.eclipse.persistence.testing.models.jpa.advanced.additionalcriteria;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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
