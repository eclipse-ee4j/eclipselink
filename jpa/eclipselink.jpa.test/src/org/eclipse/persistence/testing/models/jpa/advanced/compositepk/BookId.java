/*******************************************************************************
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     09/11/2017-2.1 Will Dazey 
 *       - 520387: multiple owning descriptors for an embeddable are not set
 ******************************************************************************/ 
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
