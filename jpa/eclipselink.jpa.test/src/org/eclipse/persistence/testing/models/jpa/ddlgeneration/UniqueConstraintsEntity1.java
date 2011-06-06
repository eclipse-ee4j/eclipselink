/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     11/06/2009-2.0 Guy Pelletier 
 *       - 286317: UniqueConstraint xml element is changing (plus couple other fixes, see bug)
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Column;

/**
 * Unique constraints test model. Unique constraints are given by metadata annotations. 
 */
@Entity
@Table(name="DDL_UCENTITY1", uniqueConstraints = {
    @UniqueConstraint(name="UCforColumn2", columnNames={"column2"}),
    @UniqueConstraint(columnNames={"column31", "column32"})
})
public class UniqueConstraintsEntity1 implements Serializable {

    @Id
    private Integer id;
    
    @Column(unique=true, nullable=false)
    private Integer column1;
    @Column(nullable=false)
    private Integer column2;
    @Column(nullable=false)
    private Integer column31;
    @Column(nullable=false)
    private Integer column32;

    public UniqueConstraintsEntity1() {
    }

    public UniqueConstraintsEntity1(Integer id) {
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
