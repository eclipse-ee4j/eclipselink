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
//     07/30/2020 - Will Dazey
//       - 564260: ElementCollection lowercase AttributeOverride is ignored
package org.eclipse.persistence.jpa.test.mapping.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class OverrideEmbeddableB implements Serializable{
    private static final long serialVersionUID = 1L;

    @Column(name = "value")
    private Integer value;

    @Column(name = "value2")
    private Integer value2;

    private OverrideNestedEmbeddableB nestedValue;

    public OverrideEmbeddableB() { }

    public OverrideEmbeddableB(Integer value, Integer value2, OverrideNestedEmbeddableB nestedValue) {
        this.value = value;
        this.value2 = value2;
        this.nestedValue = nestedValue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((nestedValue == null) ? 0 : nestedValue.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        result = prime * result + ((value2 == null) ? 0 : value2.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OverrideEmbeddableB other = (OverrideEmbeddableB) obj;
        if (nestedValue == null) {
            if (other.nestedValue != null)
                return false;
        } else if (!nestedValue.equals(other.nestedValue))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        if (value2 == null) {
            if (other.value2 != null)
                return false;
        } else if (!value2.equals(other.value2))
            return false;
        return true;
    }
} 