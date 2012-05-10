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
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Unique constraints test model. Unique constraints are given by XML descriptor. 
 */
@Entity
// Followings are given by XML descriptor.
//@Table(name="DDL_UCENTITY2", uniqueConstraints = {
//    @UniqueConstraint(columnNames={"column2"}),
//    @UniqueConstraint(columnNames={"column31", "column32"})
//})
public class UniqueConstraintsEntity2 implements Serializable {

    @Id
    private Integer id;

    private Integer column1;
    private Integer column2;
    private Integer column31;
    private Integer column32;

    public UniqueConstraintsEntity2() {
    }

    public UniqueConstraintsEntity2(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public Integer getColumn1() {
        return column1;
    }

    public void setColumn1(Integer column1) {
        this.column1 = column1;
    }

    public Integer getColumn2() {
        return column2;
    }

    public void setColumn2(Integer column2) {
        this.column2 = column2;
    }

    public Integer getColumn31() {
        return column31;
    }

    public void setColumn31(Integer column31) {
        this.column31 = column31;
    }

    public Integer getColumn32() {
        return column32;
    }

    public void setColumn32(Integer column32) {
        this.column32 = column32;
    }

    public void setColumns(Integer col1, Integer col2, Integer col31, Integer col32) {
        setColumn1(col1);
        setColumn2(col2);
        setColumn31(col31);
        setColumn32(col32);
    }
}
