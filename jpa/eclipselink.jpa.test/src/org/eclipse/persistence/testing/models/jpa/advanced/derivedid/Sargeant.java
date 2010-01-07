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
 *     04/24/2009-2.0 Guy Pelletier 
 *       - 270011: JPA 2.0 MappedById support
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.advanced.derivedid;

import static javax.persistence.GenerationType.TABLE;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * This model tests Example #1 of the mapsId cases (mapped from MasterCorporal)
 * 
 * @author gpelleti
 */
@Entity
@Table(name="JPA_SARGEANT")
public class Sargeant {
    @Id
    @Column(name="ID")
    @GeneratedValue(strategy=TABLE, generator="SARGEANT_TABLE_GENERATOR")
    @TableGenerator(
        name="SARGEANT_TABLE_GENERATOR", 
        table="JPA_SARGEANT_SEQ", 
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="SARGEANT_SEQ",
        initialValue=50
    )
    long sargeantId;

    @Basic
    @Column(name="NAME")
    String name;
    
    public String getName() {
        return name;
    }
    
    public long getSargeantId() {
        return sargeantId;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setSargeantId(long sargeantId) {
        this.sargeantId = sargeantId;
    }
}
