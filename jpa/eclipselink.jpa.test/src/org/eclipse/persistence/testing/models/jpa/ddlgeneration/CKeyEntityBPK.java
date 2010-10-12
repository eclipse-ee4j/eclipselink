/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author Wonseok Kim
 */
@Embeddable
public class CKeyEntityBPK {
    @Column(name = "SEQ")
    public long seq;

    @Column(name = "CODE", length=64)
    public String code;

    public CKeyEntityBPK() {
    }

    public CKeyEntityBPK(long seq, String code) {
        this.seq = seq;
        this.code = code;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CKeyEntityBPK that = (CKeyEntityBPK) o;

        return seq == that.seq && code.equals(that.code);
    }

    public int hashCode() {
        int result;
        result = (int) (seq ^ (seq >>> 32));
        result = 31 * result + code.hashCode();
        return result;
    }
}
