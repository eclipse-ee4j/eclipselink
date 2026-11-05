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
package org.eclipse.persistence.jpa.test.criteria.model;

import java.io.Serializable;
import java.util.Objects;

public class PluralCriteriaElementId implements Serializable {
    public int firstId;
    public int secondId;

    public PluralCriteriaElementId() {
    }

    public PluralCriteriaElementId(int firstId, int secondId) {
        this.firstId = firstId;
        this.secondId = secondId;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof PluralCriteriaElementId id && firstId == id.firstId && secondId == id.secondId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstId, secondId);
    }
}
