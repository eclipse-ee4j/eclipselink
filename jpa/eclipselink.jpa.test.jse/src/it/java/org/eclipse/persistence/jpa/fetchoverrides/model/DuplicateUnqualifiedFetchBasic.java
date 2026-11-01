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
package org.eclipse.persistence.jpa.fetchoverrides.model;

import jakarta.persistence.Basic;
import jakarta.persistence.Fetch;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Registered only by the negative test's ORM XML, never by weaving autodiscovery.
@Table(name = "JPA_UF_INVALID_BASIC")
public class DuplicateUnqualifiedFetchBasic {
    @Id
    private int id;

    @Basic
    @Fetch
    @Fetch(type = FetchType.LAZY)
    private String label;
}
