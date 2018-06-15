/*
 * Copyright (c) 2018 Oracle and/or its affiliates, IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     02/20/2018-2.7 Will Dazey
//       - 531062: Incorrect expression type created for CollectionExpression
package org.eclipse.persistence.jpa.test.jpql.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class JPQLEntityId implements Serializable {

    private static final long serialVersionUID = 1L;

    private JPQLEmbeddedValue value1;  
    private JPQLEmbeddedValue value2;

    public JPQLEntityId() { }

    public JPQLEntityId(JPQLEmbeddedValue value1, JPQLEmbeddedValue value2) {
        this.value1 = value1;
        this.value2 = value2;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value1 == null) ? 0 : value1.hashCode());
        result = prime * result + ((value2 == null) ? 0 : value2.hashCode());
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
        JPQLEntityId other = (JPQLEntityId) obj;
        if (value1 == null) {
            if (other.value1 != null)
                return false;
        } else if (!value1.equals(other.value1))
            return false;
        if (value2 == null) {
            if (other.value2 != null)
                return false;
        } else if (!value2.equals(other.value2))
            return false;
        return true;
    }
}
