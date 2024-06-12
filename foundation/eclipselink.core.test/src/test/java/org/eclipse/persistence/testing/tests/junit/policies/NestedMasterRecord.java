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
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.tests.junit.policies;

import java.util.Objects;

public record NestedMasterRecord(int intAttribute, String stringAttribute, NestedDetailRecord nestedDetailRecord) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NestedMasterRecord that = (NestedMasterRecord) o;
        return intAttribute == that.intAttribute && Objects.equals(stringAttribute, that.stringAttribute) && Objects.equals(nestedDetailRecord, that.nestedDetailRecord);
    }

    @Override
    public int hashCode() {
        return Objects.hash(intAttribute, stringAttribute, nestedDetailRecord);
    }
}
