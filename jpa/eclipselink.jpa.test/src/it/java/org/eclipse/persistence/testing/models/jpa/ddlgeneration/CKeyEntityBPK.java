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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

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
