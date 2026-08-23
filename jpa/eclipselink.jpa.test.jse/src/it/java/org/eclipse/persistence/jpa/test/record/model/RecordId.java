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

package org.eclipse.persistence.jpa.test.record.model;

import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public record RecordId(UUID id) {

    /**
     * Custom no-arg constructor that generates a new identifier. This mirrors
     * the original #2656 reproduction, where the no-arg constructor let
     * EclipseLink instantiate the record before it failed setting the final
     * {@code id} field.
     */
    public RecordId() {
        this(UUID.randomUUID());
    }
}
