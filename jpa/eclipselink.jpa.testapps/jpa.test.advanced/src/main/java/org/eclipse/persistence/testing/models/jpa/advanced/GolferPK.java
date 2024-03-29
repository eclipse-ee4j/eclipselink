/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.advanced;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class GolferPK implements java.io.Serializable {
    private int id;

    public GolferPK() {}

    public GolferPK(int id) {
        this.id = id;
    }

    @Column(name="WORLDRANK_ID", nullable=false, insertable=false, updatable=false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String toString() {
        return "GolferPK id(" + id + ")";
    }
}
