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
package org.eclipse.persistence.jpa.test.query.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class OrderByKey implements Serializable {

    private static final long serialVersionUID = 1L;

    private long keyA;

    private long keyB;

    public OrderByKey() {
    }

    public OrderByKey(long keyA, long keyB) {
        this.keyA = keyA;
        this.keyB = keyB;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof OrderByKey other && keyA == other.keyA && keyB == other.keyB;
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyA, keyB);
    }
}
