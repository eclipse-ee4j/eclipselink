/*
 * Copyright (c) 2019, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     06/19/2019 - Will Dazey
//       - 391279: Add support for Unidirectional OneToMany mappings with non-nullable values
package org.eclipse.persistence.jpa.test.mapping.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

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
