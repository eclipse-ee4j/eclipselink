/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;
import javax.persistence.Table;

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
