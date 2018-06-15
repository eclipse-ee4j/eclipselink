/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     tware - testing for DDL issue with embedded and MTM
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Patent {

    @Id
    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
