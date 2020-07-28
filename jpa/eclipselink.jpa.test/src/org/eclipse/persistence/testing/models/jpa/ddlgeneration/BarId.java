/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2010, 2015 Karsten Wutzke. All rights reserved.
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
//     12/23/2010-2.3 Guy Pelletier submitted for Karsten Wutzke
//       - 331386: NPE when mapping chain of 2 multi-column relationships using JPA 2.0 and @EmbeddedId composite PK-FK
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
public class BarId implements Serializable {
    @Embedded
    private FooId fooId;

    @Column(name = "numba")
    private Integer numba;
}
