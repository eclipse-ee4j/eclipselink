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
 *     03/08/2010-2.1 Michael O'Brien 
 *       - 300051: JPA 2.0 Metamodel processing requires EmbeddedId validation moved higher from
 *                      EmbeddedIdAccessor.process() to MetadataDescriptor.addAccessor() so we
 *                      can better determine when to add the MAPPED_SUPERCLASS_RESERVED_PK_NAME
 *                      temporary PK field used to process MappedSuperclasses for the Metamodel API
 *                      during MetadataProject.addMetamodelMappedSuperclass()
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.metamodel;

import static javax.persistence.GenerationType.TABLE;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.TableGenerator;

@Embeddable
public class CPUEmbeddedId implements Serializable {
    private static final long serialVersionUID = 2162087921393149126L;

    private int pk_part1;

    // This class is embedded inside a CPU MappedSuperclass (MultiCoreCPU Entity)
    @GeneratedValue(strategy=TABLE, generator="CPUEMBEDID_MM_TABLE_GENERATOR")
    @TableGenerator(
        name="CPUEMBEDID_MM_TABLE_GENERATOR", 
        table="CMP3_MM_CPUEMBEDDID_SEQ", 
        pkColumnName="SEQ_MM_NAME", 
        valueColumnName="SEQ_MM_COUNT",
        pkColumnValue="CUST_MM_SEQ"
    )
    @Column(name = "CPU_ID", nullable = false)
    public int getPk_part1() {
        return pk_part1;
    }

    public void setPk_part1(int pkPart1) {
        pk_part1 = pkPart1;
    }

    public CPUEmbeddedId() {
    }
    
    @Override
    public boolean equals(Object anEmbeddedId) {
        if (anEmbeddedId.getClass() != CPUEmbeddedId.class) {
            return false;
        }        
        CPUEmbeddedId embeddedId = (CPUEmbeddedId) anEmbeddedId;        
        return ((CPUEmbeddedId)anEmbeddedId).pk_part1 == this.pk_part1;                
    }

    @Override
    public int hashCode() {
        return 9232 * pk_part1;
    }
        
}

