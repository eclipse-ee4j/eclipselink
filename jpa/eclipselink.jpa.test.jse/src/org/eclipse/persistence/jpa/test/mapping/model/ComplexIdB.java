/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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
//     09/12/2018 - Will Dazey
//       - 391279: Add support for Unidirectional OneToMany mappings with non-nullable values
package org.eclipse.persistence.jpa.test.mapping.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class ComplexIdB implements Serializable {
    private static final long serialVersionUID = 1L;

    private long idA;
    private long idB;

    public ComplexIdB() {
        this.idA = 0;
        this.idB = 0;
    }

    public ComplexIdB(long aId, long bId) {
        this.idA = aId;
        this.idB = bId;
    }

    public long getIdA() {
        return idA;
    }

    public void setIdA(long idA) {
        this.idA = idA;
    }

    public long getIdB() {
        return idB;
    }

    public void setIdB(long idB) {
        this.idB = idB;
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
        ComplexIdB other = (ComplexIdB) obj;
        if (idA != other.idA)
            return false;
        if (idB != other.idB)
            return false;
        return true;
    }
}
