/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2012, 2015 Arron Ferguson. All rights reserved.
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
//     05/17/2012-2.3.3 Arron Ferguson
//       - 379829: NPE Thrown with OneToOne Relationship
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import java.io.Serializable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "sponsor")
public class Sponsor implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private Address2 address;
}
