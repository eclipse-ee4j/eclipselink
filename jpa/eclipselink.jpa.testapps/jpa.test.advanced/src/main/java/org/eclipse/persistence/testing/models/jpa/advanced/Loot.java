/*
 * Copyright (c) 2011, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     10/27/2010-2.2 Guy Pelletier
//       - 328114: @AttributeOverride does not work with nested embeddables having attributes of the same name
package org.eclipse.persistence.testing.models.jpa.advanced;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

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
