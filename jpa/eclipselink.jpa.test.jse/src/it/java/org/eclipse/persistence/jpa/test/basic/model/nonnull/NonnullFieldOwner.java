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
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "NNOPT_FIELD")
@Access(AccessType.FIELD)
public class NonnullFieldOwner extends NonnullBase {
    @Id
    public long id;

    @Nonnull
    public String implicitBasic;

    @Nonnull
    @Basic(optional = true)
    public String explicitBasic;

    @Nonnull
    @Basic(optional = false)
    public String requiredBasic;

    public String nullableBasic;

    @Basic(optional = false)
    public String requiredControl;

    @Nonnull
    @ManyToOne(optional = true)
    @JoinColumn(name = "NN_MTO")
    public NonnullTarget requiredMany;

    @ManyToOne
    @JoinColumn(name = "OPT_MTO")
    public NonnullTarget optionalMany;

    @ManyToOne(optional = false)
    @JoinColumn(name = "CTRL_MTO")
    public NonnullTarget requiredManyControl;

    @Nonnull
    @OneToOne(optional = true)
    @JoinColumn(name = "NN_OTO")
    public NonnullTarget requiredOne;

    @OneToOne
    @JoinColumn(name = "OPT_OTO")
    public NonnullTarget optionalOne;

    @OneToOne(optional = false)
    @JoinColumn(name = "CTRL_OTO")
    public NonnullTarget requiredOneControl;

    @Embedded
    public NonnullDetails details;

    public String ignoredGetterValue;

    @Nonnull
    public String getIgnoredGetterValue() {
        return ignoredGetterValue;
    }

    @Nonnull
    @Transient
    public String transientValue;
}
