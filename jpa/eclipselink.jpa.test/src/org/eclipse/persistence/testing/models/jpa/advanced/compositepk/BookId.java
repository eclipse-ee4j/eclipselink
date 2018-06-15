/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.models.jpa.advanced.compositepk;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
public class BookId implements Serializable {

    private static final long serialVersionUID = -2930906625230816968L;

    @Embedded
    private NumberId numberId;

    public BookId() {
        this.numberId = new NumberId();
    }

    @Override
    public int hashCode() {
        return numberId.hashCode();
    }

    public NumberId getNumberId() {
        return numberId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BookId other = (BookId) obj;
        if (numberId == null) {
            if (other.numberId != null)
                return false;
        } else if (!numberId.equals(other.numberId))
            return false;
        return true;
    }

    public String toString() {
        return "BookId [numberId=" + this.numberId + "]";
    }

}
