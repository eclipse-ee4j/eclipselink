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
//     10/27/2010-2.2 Guy Pelletier
//       - 328114: @AttributeOverride does not work with nested embeddables having attributes of the same name
package org.eclipse.persistence.testing.models.jpa.advanced;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="JPA_LOOT")
public class Loot implements Serializable {
    @Id
    @GeneratedValue
    public int id;

    @Embedded
    @AttributeOverrides ({
        @AttributeOverride(name="quantity.value", column=@Column(name="QTY_VALUE")),
        @AttributeOverride(name="cost.value", column=@Column(name="COST_VALUE"))
    })
    public Bag bag;
}
