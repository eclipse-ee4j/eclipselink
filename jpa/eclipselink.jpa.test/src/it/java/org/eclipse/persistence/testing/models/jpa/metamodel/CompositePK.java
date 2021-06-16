/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     07/16/2009-2.0  mobrien - JPA 2.0 Metadata API test model
//       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
package org.eclipse.persistence.testing.models.jpa.metamodel;

import java.io.Serializable;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class CompositePK implements Serializable {

    @Basic(optional = false)
    @Column(name = "pk_field1", nullable = false)
    private int pk_field1;

    @Basic(optional = false)
    @Column(name = "pk_field2", nullable = false)
    private int pk_field2;

    public CompositePK() {
    }

    @Override
    public boolean equals(Object aCompositePK) {
        if (aCompositePK.getClass() != CompositePK.class) {
            return false;
        }
        CompositePK compositePK = (CompositePK) aCompositePK;
        return (compositePK.pk_field1 == this.pk_field1) &&
                (compositePK.pk_field2 == this.pk_field2);
    }

    @Override
    public int hashCode() {
        return 9232 * pk_field1 * pk_field2;
    }
}

