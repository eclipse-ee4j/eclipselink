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

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.PrimaryKeyJoinColumns;
import jakarta.persistence.Table;

import org.eclipse.persistence.annotations.CascadeOnDelete;

/**
 * @author Wonseok Kim
 */
@Entity
@Table(name="DDL_CKENTB2")
@PrimaryKeyJoinColumns({
    @PrimaryKeyJoinColumn(name="SEQ", referencedColumnName="SEQ"),
    @PrimaryKeyJoinColumn(name="CODE", referencedColumnName="CODE")
})
@CascadeOnDelete
public class CKeyEntityB2 extends CKeyEntityB {

    public CKeyEntityB2() {
    }

    public CKeyEntityB2(CKeyEntityBPK key) {
        super(key);
    }
}
