/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
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
//     06/19/2019 - Will Dazey
//       - 391279: Add support for Unidirectional OneToMany mappings with non-nullable values
package org.eclipse.persistence.jpa.test.mapping.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ComplexIdD implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "COMPLEXID_D_A", nullable = false)
    private long idA;

    @Column(name = "COMPLEXID_D_B", nullable = false)
    private long idB;

    public ComplexIdD() {
        this.idA = 0;
        this.idB = 0;
    }

    public ComplexIdD(long aId, long bId) {
        this.idA = aId;
        this.idB = bId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (idA ^ (idA >>> 32));
        result = prime * result + (int) (idB ^ (idB >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ComplexIdD other = (ComplexIdD) obj;
        if (idA != other.idA)
            return false;
        if (idB != other.idB)
            return false;
        return true;
    }
}