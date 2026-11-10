/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.test.basic.model.nonnull;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "NNOPT_PK")
public class NonnullPrimaryKeyOwner {
    @Id
    public long id;

    @Nonnull
    @OneToOne(optional = true)
    @PrimaryKeyJoinColumn(name = "ID", referencedColumnName = "ID")
    public NonnullTarget requiredTarget;

    @OneToOne
    @PrimaryKeyJoinColumn(name = "ID", referencedColumnName = "ID")
    public NonnullFieldOwner optionalOwner;
}
