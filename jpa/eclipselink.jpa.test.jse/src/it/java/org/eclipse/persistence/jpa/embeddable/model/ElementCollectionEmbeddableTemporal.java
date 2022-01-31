/*
 * Copyright (c) 2021 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.embeddable.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Embeddable
public class ElementCollectionEmbeddableTemporal {

    @Temporal(value = TemporalType.DATE)
    @Column(name = "TEMPORALVALUE", columnDefinition = "DATE")
    private Date temporalValue;

    public ElementCollectionEmbeddableTemporal() { }

    public ElementCollectionEmbeddableTemporal(Date temporalValue) {
        this.temporalValue = temporalValue;
    }
}
