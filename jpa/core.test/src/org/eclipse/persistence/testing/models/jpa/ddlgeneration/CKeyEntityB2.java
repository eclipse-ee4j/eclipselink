/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;
import javax.persistence.Table;

/**
 * @author Wonseok Kim
 */
@Entity
@Table(name="DDL_CKENTB2")
@PrimaryKeyJoinColumns({
    @PrimaryKeyJoinColumn(name="SEQ", referencedColumnName="SEQ"),
    @PrimaryKeyJoinColumn(name="CODE", referencedColumnName="CODE")
})
public class CKeyEntityB2 extends CKeyEntityB {

    public CKeyEntityB2() {
    }

    public CKeyEntityB2(CKeyEntityBPK key) {
        super(key);
    }
}
