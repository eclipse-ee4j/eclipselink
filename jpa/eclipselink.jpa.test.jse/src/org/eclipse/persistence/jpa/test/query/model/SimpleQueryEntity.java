/*
 * Copyright (c) 2021 IBM Corporation, Oracle, and/or affiliates. All rights reserved.
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
//     IBM - Bug 521402: Add support for Criteria queries with only literals
package org.eclipse.persistence.jpa.test.query.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class SimpleQueryEntity {

    @Id
    private long id;

    private Integer intVal1;
    private String strVal1;

    public SimpleQueryEntity() { }

    public SimpleQueryEntity(long id, Integer intVal1, String strVal1) {
        this.id = id;
        this.intVal1 = intVal1;
        this.strVal1 = strVal1;
    }
}