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
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "NNOPT_XML")
public class NonnullXmlOwner extends NonnullXmlBase {
    @Id
    public long id;

    @Nonnull
    public String basicTrue;

    @Nonnull
    public String basicFalse;

    @Nonnull
    public String basicOmitted;

    @Nonnull
    public String annotationOnly;

    @Nonnull
    @ManyToOne(optional = true)
    @JoinColumn(name = "MANY_TRUE")
    public NonnullTarget manyTrue;

    @Nonnull
    @ManyToOne(optional = true)
    @JoinColumn(name = "MANY_FALSE")
    public NonnullTarget manyFalse;

    @Nonnull
    @OneToOne(optional = true)
    @JoinColumn(name = "ONE_TRUE")
    public NonnullTarget oneTrue;

    @Nonnull
    @OneToOne(optional = true)
    @JoinColumn(name = "ONE_FALSE")
    public NonnullTarget oneFalse;

    @Embedded
    public NonnullXmlDetails details;
}
