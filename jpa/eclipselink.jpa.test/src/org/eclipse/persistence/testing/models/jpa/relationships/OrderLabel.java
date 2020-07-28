/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa.relationships;

import javax.persistence.*;
import static javax.persistence.GenerationType.*;

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

