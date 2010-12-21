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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.TABLE;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * Composite key Entity.
 * 
 * @author Wonseok Kim
 */
@Entity
@IdClass(CKeyEntityAPK.class)
@Table(name = "DDL_CKENTA")
@TableGenerator(
    name = "CKEYENTITY_TABLE_GENERATOR",
    table = "DDL_CKENT_SEQ",
    pkColumnName = "SEQ_NAME",
    valueColumnName = "SEQ_COUNT",
    pkColumnValue = "CKENT_SEQ"
)
public class CKeyEntityA {
    @Id
    @GeneratedValue(strategy = TABLE, generator = "CKEYENTITY_TABLE_GENERATOR")
    @Column(name = "SEQ")
    private int seq;

    @Id
    @Column(name = "F_NAME", length=64)
    private String firstName;

    @Id
    @Column(name = "L_NAME", length=64)
    private String lastName;

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name="B_SEQ", referencedColumnName = "SEQ"),
        @JoinColumn(name="B_CODE", referencedColumnName = "CODE")
    })
    private CKeyEntityB bs;
    
    @OneToOne(mappedBy="a")
    private CKeyEntityC c;
    
    // Relationship using candidate(unique) keys
    // For testing whether a generated FK constraint has reordered unique keys according to target table.
    // CKeyEntityB has unique constraint ("UNQ2", "UNQ1").
    @OneToOne
    @JoinColumns({
        @JoinColumn(name="B_UNQ1", referencedColumnName = "UNQ1"),
        @JoinColumn(name="B_UNQ2", referencedColumnName = "UNQ2")
    })
    private CKeyEntityB uniqueB;
    

    public CKeyEntityA() {
    }

    public CKeyEntityA(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getSeq() {
        return seq;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public CKeyEntityAPK getKey() {
        return new CKeyEntityAPK(seq, firstName, lastName);
    }
    
    public CKeyEntityB getBs() {
        return bs;
    }

    public void setBs(CKeyEntityB bs) {
        this.bs = bs;
    }

    public CKeyEntityC getC() {
        return c;
    }

    public void setC(CKeyEntityC c) {
        this.c = c;
    }

    public CKeyEntityB getUniqueB() {
        return uniqueB;
    }

    public void setUniqueB(CKeyEntityB uniqueB) {
        this.uniqueB = uniqueB;
    }
}
