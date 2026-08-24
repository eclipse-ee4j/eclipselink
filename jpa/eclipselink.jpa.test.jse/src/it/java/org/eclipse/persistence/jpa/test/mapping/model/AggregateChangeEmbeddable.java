/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
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
//     07/23/2026 - Andreas Lemmer
//       - 1455: Do not calculate changes for aggregates registered in the UnitOfWork
package org.eclipse.persistence.jpa.test.mapping.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class AggregateChangeEmbeddable {

    @Column(name = "EMBEDDED_VALUE")
    private String value;

    public AggregateChangeEmbeddable() { }

    public AggregateChangeEmbeddable(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return (value == null) ? 0 : value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AggregateChangeEmbeddable)) {
            return false;
        }
        AggregateChangeEmbeddable other = (AggregateChangeEmbeddable) obj;
        return (value == null) ? (other.value == null) : value.equals(other.value);
    }
}
