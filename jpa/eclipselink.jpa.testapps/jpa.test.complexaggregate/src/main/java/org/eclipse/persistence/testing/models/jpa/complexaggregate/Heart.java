/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     12/17/2010-2.2 Guy Pelletier
//       - 330755: Nested embeddables can't be used as embedded ids
package org.eclipse.persistence.testing.models.jpa.complexaggregate;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class Heart {
    @Column(name="H_SIZE")
    public int size;

    public Heart() {}

    public Heart(int size) {
        this.size = size;
    }

    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof Heart heart) {

            return size == heart.getSize();
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(size);
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
