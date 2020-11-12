/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2020 IBM Corporation. All rights reserved.
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
//     01/06/2020 - Will Dazey
//       - 347987: Fix Attribute Override for Complex Embeddables
package org.eclipse.persistence.jpa.test.mapping.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class OverrideEmbeddableA implements Serializable{
    private static final long serialVersionUID = 1L;

    @Column(name = "VALUE")
    private Integer value;

    @Column(name = "VALUE2")
    private Integer value2;

    private OverrideNestedEmbeddableA nestedValue;

    public OverrideEmbeddableA() { }

    public OverrideEmbeddableA(Integer value, Integer value2, OverrideNestedEmbeddableA nestedValue) {
        this.value = value;
        this.value2 = value2;
        this.nestedValue = nestedValue;
    }
}
