/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates, Laird Nelson. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     01/07/2010-2.0.1 Laird Nelson
//       - 282075: DDL generation is unpredictable
//     04/04/2013-2.4.3 Guy Pelletier
//       - 388564: Generated DDL does not match annotation
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import java.io.Serializable;

import javax.persistence.*;

import org.eclipse.persistence.annotations.Index;

/**
 * This class is only usable within the ddlTableSuffix persistence unit since
 * the columnDefinition prevents it from being created on DB2
 */
@Entity(name = "One")
@Table(name = "o")
public class One implements Serializable {
    @Id
    // Note the column definition here, per the spec trumps the other variables length, scale etc.
    @Column(name = "id", length = 4, scale = 10, precision = 0, columnDefinition = "NUMERIC(10) NOT NULL", nullable = false)
    private long id;

    @Column(name = "text", length = 10)
    @Index
    private String text;

    public One() {
        super();
    }

    public One(final long id) {
        super();
        this.id = id;
    }

    public String getText() {
        return this.text;
    }

    public void setText(final String text) {
        this.text = text;
    }
}
