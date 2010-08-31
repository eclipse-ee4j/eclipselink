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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Index;

import java.util.Collection;

/**
 * Composite Key Entity.
 * 
 * @author Wonseok Kim
 */
@Entity
@Table(name = "DDL_CKENTC")
public class CKeyEntityC {

    @EmbeddedId
    private CKeyEntityCPK key;
    
    // Test for GF#1392
    // If there is a same name column for the entity and many-to-many table, wrong pk constraint generated.
    @Column(name="C_ROLE")
    private String tempRole;

    @OneToOne
    @JoinColumns({
        @JoinColumn(name="A_SEQ", referencedColumnName = "SEQ"),
        @JoinColumn(name="A_L_NAME", referencedColumnName = "L_NAME"),
        @JoinColumn(name="A_F_NAME", referencedColumnName = "F_NAME")
    })
    private CKeyEntityA a;
    
    @ManyToMany
        @JoinTable(name="DDL_CKENT_C_B",
        joinColumns={
            @JoinColumn(name="C_SEQ", referencedColumnName="SEQ"),
            @JoinColumn(name="C_ROLE", referencedColumnName="C_ROLE")
        },
        inverseJoinColumns={
            @JoinColumn(name="B_SEQ", referencedColumnName = "SEQ"),
            @JoinColumn(name="B_CODE", referencedColumnName = "CODE")
        }
    )
    @Index(name="INDEX_BS", table="DDL_CKENT_C_B", columnNames={"C_SEQ", "C_ROLE"})
    private Collection<CKeyEntityB> bs;


    public CKeyEntityC() {
    }

    public CKeyEntityC(CKeyEntityCPK key) {
        this.key = key;
    }

    public CKeyEntityCPK getKey() {
        return key;
    }

    public String getTempRole() {
        return tempRole;
    }

    public void setTempRole(String tempRole) {
        this.tempRole = tempRole;
    }

    public CKeyEntityA getA() {
        return a;
    }

    public void setA(CKeyEntityA a) {
        this.a = a;
    }

    public Collection<CKeyEntityB> getBs() {
        return bs;
    }

    public void setBs(Collection<CKeyEntityB> bs) {
        this.bs = bs;
    }
}
