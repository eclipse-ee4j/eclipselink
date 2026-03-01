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

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public record RecordPK (int id_int, String id_string) implements Serializable {

    public RecordPK() {
        this(0,null);
    }

    public RecordPK(int id_int, String id_string) {
        this.id_int = id_int;
        this.id_string = id_string;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecordPK recordPK = (RecordPK) o;
        return id_int == recordPK.id_int && Objects.equals(id_string, recordPK.id_string);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_int, id_string);
    }
}
