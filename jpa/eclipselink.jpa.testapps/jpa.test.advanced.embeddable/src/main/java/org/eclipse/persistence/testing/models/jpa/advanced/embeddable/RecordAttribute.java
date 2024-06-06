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
import jakarta.persistence.Embedded;

import java.util.Objects;

@Embeddable
public record RecordAttribute(@Embedded RecordNestedAttribute nestedAttribute, String name3) {

    public RecordAttribute() {
        this(null, null);
    }

    public RecordAttribute(RecordNestedAttribute nestedAttribute, String name3) {
        this.nestedAttribute = nestedAttribute;
        this.name3 = name3;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecordAttribute that = (RecordAttribute) o;
        return Objects.equals(nestedAttribute, that.nestedAttribute) && Objects.equals(name3, that.name3);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nestedAttribute, name3);
    }
}
