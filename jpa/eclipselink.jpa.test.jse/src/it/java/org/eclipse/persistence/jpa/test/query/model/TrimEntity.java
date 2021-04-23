/*
 * Copyright (c) 2021 IBM Corporation and/or affiliates. All rights reserved.
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
//     IBM - Bug 573094: TRIM function generates incorrect SQL for CriteriaBuilder
package org.eclipse.persistence.jpa.test.query.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class TrimEntity {

    @Id
    private long id;

    private String strVal1;

    public TrimEntity() { }

    public TrimEntity(Long id, String strVal1) {
        this.id = id;
        this.strVal1 = strVal1;
    }
}