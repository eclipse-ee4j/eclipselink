/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;


@Entity
@Table(name = "address2")
public class Address2 implements Serializable {

    @Id
    private Long id;

    @MapsId
    @OneToOne(mappedBy = "address")
    private Sponsor sponsor;

}
