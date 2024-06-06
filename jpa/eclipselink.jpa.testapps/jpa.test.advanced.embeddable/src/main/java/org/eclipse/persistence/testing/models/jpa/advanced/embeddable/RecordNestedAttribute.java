/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - Initial API and implementation.
package org.eclipse.persistence.testing.models.jpa.advanced.embeddable;

import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public record RecordNestedAttribute(String name1, String name2) {

    public RecordNestedAttribute() {
        this(null, null);
    }

    public RecordNestedAttribute(String name1, String name2) {
        this.name1 = name1;
        this.name2 = name2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecordNestedAttribute that = (RecordNestedAttribute) o;
        return Objects.equals(name1, that.name1) && Objects.equals(name2, that.name2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name1, name2);
    }
}
