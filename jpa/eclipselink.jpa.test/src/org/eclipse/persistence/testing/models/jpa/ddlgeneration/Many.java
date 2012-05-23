/*******************************************************************************
 * Copyright (c) 2010  Laird Nelson . All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     01/07/2010-2.0.1 Laird Nelson  
 *       - 282075: DDL generation is unpredictable
 *     11/17/2010-2.2.0 Chris Delahunt 
 *       - 214519: Allow appending strings to CREATE TABLE statements
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

/**
 * This class is only usable within the ddlTableSuffix persistence unit
 */
@Entity(name = "Many")
@Table(name = "m")
public class Many implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", scale = 10, precision = 0, nullable = false)
    private long id;
    
    @ManyToMany
    @JoinTable(name="DDL_MANY_MANY")
    private List<Many> many;

    @ManyToOne
    @JoinColumn(name = "o", referencedColumnName = "id")
    private One one;

    public Many() {
        super();
    }

    public List<Many> getMany() {
        return many;
    }

    public void setMany(List<Many> many) {
        this.many = many;
    }

    public void setOne(final One one) {
        this.one = one;
    }

    public One getOne() {
        return this.one;
    }
}