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
package org.eclipse.persistence.testing.models.jpa.relationships;

import jakarta.persistence.*;
import static jakarta.persistence.GenerationType.*;

@Entity(name="OrderLabel")
@Table(name="JPA_ORDER_LABEL")
public class OrderLabel implements java.io.Serializable {
    private Integer orderLabelId;
    private String description;

    public OrderLabel() {}

    @Id
    @GeneratedValue(strategy=TABLE, generator="ORDER_LABEL_TABLE_GENERATOR")
    @TableGenerator(
        name="ORDER_LABEL_TABLE_GENERATOR",
        table="CMP3_CUSTOMER_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="ORDER_LABEL_SEQ"
    )
    @Column(name="ID")
    public Integer getOrderLabelId() {
        return orderLabelId;
    }

    public void setOrderLabelId(Integer id) {
        orderLabelId = id;
    }

    @Column(name="DESCRIP")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

